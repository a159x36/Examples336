package nz.ac.massey.examples336.touchbubbles

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.sqrt


const val X=0
const val Y=1
const val VX=2
const val VY=3
const val RR=4
const val CR=5


abstract class BubbleData {
    abstract operator fun set(index:Int, value: Float)
    abstract operator fun get(index:Int):Float
}

class BubbleDataFloatBuffer(size: Int) : BubbleData() {
    val bubbleData = ByteBuffer.allocateDirect(size*4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    override fun set(index: Int, value: Float) {
        bubbleData.put(index,value)
    }
    override fun get(index: Int): Float {
        return bubbleData[index]
    }
}

class BubbleDataFloatArray(size: Int) : BubbleData() {
    val bubbleData=FloatArray(size)
    override fun set(index: Int, value: Float) {
        bubbleData[index]=value
    }
    override fun get(index: Int): Float {
        return bubbleData[index]
    }
}

class Bubbles(viewModel: SettingsViewModel) {


    private lateinit var bubbleData:BubbleData

    /*
    operator fun FloatBuffer.set(index:Int, value: Float) {
        this.put(index,value)
    }

     */


    var mGravityX=0f
    var mGravityY=9.8f

    var csize = Size(1080f,1440f)
    var bubble:ImageBitmap?=null

    var grabbedBubbleIndex=-1

    private val viewmodel=viewModel

    private var lastvx = 0f
    private var lastvy = 0f


    fun clampWithAction(value: Float, min: Float, max: Float, run:()->Unit={}): Float {
        var newvalue=value
        if(value<min) {
            newvalue=min
            run()
        }
        if(value>max) {
            newvalue=max
            run()
        }
        return newvalue
    }

    fun updateArray(dt:Float, bubbleData: FloatArray) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value
        for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
            if(i!=grabbedBubbleIndex) {
                val newx = bubbleData[i + X] + bubbleData[i + VX] * dt * 100
                val newy = bubbleData[i + Y] + bubbleData[i + VY] * dt * 100
                bubbleData[i + X] =
                    clampWithAction(newx, bubbleData[i + CR], csize.width - bubbleData[i + CR]) {
                        bubbleData[i + VX] *= -dampening
                    }
                bubbleData[i + Y] =
                    clampWithAction(newy, bubbleData[i + CR], csize.height - bubbleData[i + CR]) {
                        bubbleData[i + VY] *= -dampening
                    }
                bubbleData[i + VX] += dt * mGravityX * 10
                bubbleData[i + VY] += dt * mGravityY * 10
            }
        }

        for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
            val x = bubbleData[i + X]
            val y = bubbleData[i + Y]
            val r = bubbleData[i + RR]
            bubbleData[i + CR] = r
            for (j in (i + 6)..(viewmodel.nbubbles.value - 1) * 6 step 6) {

                val x1 = bubbleData[j + X]
                val y1 = bubbleData[j + Y]
                val r1 = bubbleData[j + RR]
                var d = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)
                if (d < (r1 + r) * (r1 + r)) {
                    d = sqrt(d)
                    if (viewmodel.compress.value) {
                        var sz = d - bubbleData[j + CR]
                        var sz1 = d - bubbleData[i + CR]
                        if (sz < 16f)
                            sz = 16f
                        if (sz < bubbleData[i + CR])
                            bubbleData[i + CR] = sz
                        if (sz1 < 16f)
                            sz1 = 16f
                        if (sz1 < bubbleData[j + CR])
                            bubbleData[j + CR] = sz1
                    }
                    var dx = x1 - x
                    var dy = y1 - y
                    if (d != 0f) {
                        dx = dx / d
                        dy = dy / d
                    }
                    val displacement = (r + r1) - d
                    bubbleData[i + VX] =
                        (bubbleData[i + VX] - rigidity * dx * displacement) * dampening
                    bubbleData[i + VY] =
                        (bubbleData[i + VY] - rigidity * dy * displacement) * dampening
                    bubbleData[j + VX] =
                        (bubbleData[j + VX] + rigidity * dx * displacement) * dampening
                    bubbleData[j + VY] =
                        (bubbleData[j + VY] + rigidity * dy * displacement) * dampening
                }
            }
        }
    }

    fun updateBuffer(dt:Float, bubbleData: FloatBuffer) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value
        for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
            if(i!=grabbedBubbleIndex) {
                val newx = bubbleData[i + X] + bubbleData[i + VX] * dt * 100
                val newy = bubbleData[i + Y] + bubbleData[i + VY] * dt * 100
                bubbleData.put(i + X,
                    clampWithAction(newx, bubbleData[i + CR], csize.width - bubbleData[i + CR]) {
                        bubbleData.put(i + VX, bubbleData[i + VX] * -dampening)
                    })
                bubbleData.put(i + Y,
                    clampWithAction(newy, bubbleData[i + CR], csize.height - bubbleData[i + CR]) {
                        bubbleData.put(i + VY, bubbleData[i + VY] * -dampening)
                    })
                bubbleData.put(i + VX, bubbleData[i + VX] + dt * mGravityX * 10)
                bubbleData.put(i + VY, bubbleData[i + VY] + dt * mGravityY * 10)
            }
        }

        for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
            val x = bubbleData[i + X]
            val y = bubbleData[i + Y]
            val r = bubbleData[i + RR]
            bubbleData.put(i + CR,  r)
            for (j in (i + 6)..(viewmodel.nbubbles.value - 1) * 6 step 6) {

                val x1 = bubbleData[j + X]
                val y1 = bubbleData[j + Y]
                val r1 = bubbleData[j + RR]
                var d = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)
                if (d < (r1 + r) * (r1 + r)) {
                    d = sqrt(d)
                    if (viewmodel.compress.value) {
                        var sz = d - bubbleData[j + CR]
                        var sz1 = d - bubbleData[i + CR]
                        if (sz < 16f)
                            sz = 16f
                        if (sz < bubbleData[i + CR])
                            bubbleData.put(i + CR, sz)
                        if (sz1 < 16f)
                            sz1 = 16f
                        if (sz1 < bubbleData[j + CR])
                            bubbleData.put(j + CR, sz1)
                    }
                    var dx = x1 - x
                    var dy = y1 - y
                    if (d != 0f) {
                        dx = dx / d
                        dy = dy / d
                    }
                    val displacement = (r + r1) - d
                    bubbleData.put(i + VX,
                        (bubbleData[i + VX] - rigidity * dx * displacement) * dampening)
                    bubbleData.put(i + VY,
                        (bubbleData[i + VY] - rigidity * dy * displacement) * dampening)
                    bubbleData.put(j + VX,
                        (bubbleData[j + VX] + rigidity * dx * displacement) * dampening)
                    bubbleData.put(j + VY,
                        (bubbleData[j + VY] + rigidity * dy * displacement) * dampening)
                }
            }
        }
    }

    fun update(dt:Float) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value
        if(viewmodel.native.value) {
            if(viewmodel.usedirect.value)
                nativeUpdateBuffer(viewmodel.nbubbles.value,grabbedBubbleIndex,csize.width.toInt(),csize.height.toInt(),dt,mGravityX,mGravityY,(bubbleData as BubbleDataFloatBuffer).bubbleData,rigidity,dampening,viewmodel.compress.value)
            else
                nativeUpdateArray(viewmodel.nbubbles.value,grabbedBubbleIndex,csize.width.toInt(),csize.height.toInt(),dt,mGravityX,mGravityY,(bubbleData as BubbleDataFloatArray).bubbleData,rigidity,dampening,viewmodel.compress.value)
            return
        }
        if(viewmodel.usedirect.value)
            updateBuffer(dt,(bubbleData as BubbleDataFloatBuffer).bubbleData)
        else
            updateArray(dt,(bubbleData as BubbleDataFloatArray).bubbleData)

    }


    private fun inBubble(i: Int, x: Float, y: Float): Boolean {
        return (x - bubbleData[i + X]) * (x - bubbleData[i + X]) + (y - bubbleData[i + Y]) * (y - bubbleData[i + Y]) < bubbleData[i + RR] * bubbleData[i + RR]
    }

    fun grabBubble(x: Float, y: Float) {
        for(i in 0..(viewmodel.nbubbles.value-1)*6 step 6){
            if(inBubble(i,x,y)){
                grabbedBubbleIndex=i
                bubbleData[i+VX]=0f
                bubbleData[i+VY]=0f
                return
            }
        }
    }

    fun ungrabBubble() {
        if (grabbedBubbleIndex != -1) {
            bubbleData[grabbedBubbleIndex + VX] = lastvx
            bubbleData[grabbedBubbleIndex + VY] = lastvy
            grabbedBubbleIndex = -1
        }
    }

    fun moveBubble(x: Float, y: Float) {
        if(grabbedBubbleIndex!=-1) {
            lastvx = x - bubbleData[grabbedBubbleIndex + X]
            lastvy = y - bubbleData[grabbedBubbleIndex + Y]
            bubbleData[grabbedBubbleIndex + X] = x
            bubbleData[grabbedBubbleIndex + Y] = y
        }
    }

    fun getbubblexy(i:Int):IntOffset {
        return IntOffset(bubbleData[i+X].toInt(),bubbleData[i+Y].toInt())
    }

    fun getbubbleradius(i:Int):Int {
        return bubbleData[i+CR].toInt()
    }

    fun init(c:Context) {
        bubble = BitmapFactory.decodeResource(c.resources,R.drawable.bubble).asImageBitmap()
     //   bubbleData = FloatArray(viewmodel.nbubbles.value*6)
        bubbleData = if(viewmodel.usedirect.value)
            BubbleDataFloatBuffer(viewmodel.nbubbles.value*6)
        else
            BubbleDataFloatArray(viewmodel.nbubbles.value*6)
        val nsmall=viewmodel.nbubbles.value-viewmodel.nlarge.value
        for (i in 0.. (viewmodel.nbubbles.value-1)*6 step 6) {

            bubbleData[i+VX]=-5+ (0..100).random()/10f
            bubbleData[i+VY]=-5 +(0..100).random()/10f
            val r: Float = if(i<nsmall*6)
                ((1..viewmodel.smallMax.value).random()+viewmodel.smallMin.value).toFloat()
            else
                ((1..viewmodel.largeMax.value).random()+viewmodel.largeMin.value).toFloat()
            bubbleData[i+RR]=r
            bubbleData[i+CR]=r
            bubbleData[i+X]=(0..((csize.width-2*r).toInt())).random() + r
            bubbleData[i+Y]=(0..((csize.height-2*r).toInt())).random() + r
        }
    }

    external fun nativeUpdateArray(nb:Int, touched:Int, width:Int,height:Int,dt:Float,gx:Float,gy:Float,bubbles:FloatArray, rigidity:Float, dampening:Float, compress:Boolean)
    external fun nativeUpdateBuffer(nb:Int, touched:Int, width:Int,height:Int,dt:Float,gx:Float,gy:Float,bubbles:FloatBuffer, rigidity:Float, dampening:Float, compress:Boolean)

}