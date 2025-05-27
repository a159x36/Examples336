package nz.ac.massey.examples336.touchbubbles

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log.i
import android.util.Log.v
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

class Bubbles(viewModel: SettingsViewModel) {
    private var bubbleData = FloatArray(200*6)

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

    fun update(dt:Float) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value
        if(viewmodel.native.value) {
            nativeUpdate(viewmodel.nbubbles.value,grabbedBubbleIndex,csize.width.toInt(),csize.height.toInt(),dt,mGravityX,mGravityY,bubbleData,rigidity,dampening,viewmodel.compress.value)
            return
        }
        for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
            if(i!=grabbedBubbleIndex) {
                var newx = bubbleData[i + X] + bubbleData[i + VX] * dt * 100
                var newy = bubbleData[i + Y] + bubbleData[i + VY] * dt * 100
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
        bubble =BitmapFactory.decodeResource(c.resources,R.drawable.bubble).asImageBitmap()
        bubbleData = FloatArray(viewmodel.nbubbles.value*6)
        val nsmall=viewmodel.nbubbles.value-viewmodel.nlarge.value
        for (i in 0.. (viewmodel.nbubbles.value-1)*6 step 6) {
            val r: Float

            bubbleData[i+VX]=-5+ (0..100).random()/10f
            bubbleData[i+VY]=-5 +(0..100).random()/10f
            r = if(i<nsmall*6)
                ((1..viewmodel.small_max.value).random()+viewmodel.small_min.value).toFloat()
            else
                ((1..viewmodel.large_max.value).random()+viewmodel.large_min.value).toFloat()
            bubbleData[i+RR]=r
            bubbleData[i+CR]=r
            bubbleData[i+X]=(0..((csize.width-2*r).toInt())).random() + r
            bubbleData[i+Y]=(0..((csize.height-2*r).toInt())).random() + r
        }
    }

    external fun nativeUpdate(nb:Int, touched:Int, width:Int,height:Int,dt:Float,gx:Float,gy:Float,bubbles:FloatArray, rigidity:Float, dampening:Float, compress:Boolean)

}