package nz.ac.massey.examples336.touchbubbles

import androidx.compose.ui.geometry.Size
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

const val GRIDSIZE=64


class Bubbles(viewModel: SettingsViewModel) {

    var bubbleData = Array<Bubble>(200, init = { Bubble() })

    private var bubblegrid:Array<Array<Bubble?>> = Array(GRIDSIZE){Array(GRIDSIZE){null}}

    private var mGravityX=0f
    private var mGravityY=9.8f

    var neighbourhood=0f

    private var csize = Size(1080f,1440f)

//    private var grabbedBubbleIndex=-1
    private var grabbedBubble:Bubble?=null

    private val viewmodel=viewModel

    private var lastvx = 0f
    private var lastvy = 0f

    init {
        init()
    }

    fun setGravity(x:Float,y:Float) {
        mGravityX=x
        mGravityY=y
    }

    fun setCsize(sz:Size)  {
        csize=sz
    }


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

    fun toindex(x:Float, max:Float):Int {
        val ix=floor(((x+0.5f)*GRIDSIZE)/max).toInt()
        if(ix<0) return 0
        if(ix>GRIDSIZE-1) return GRIDSIZE-1
        return ix
    }

    fun narrowPhase(b: Bubble, ba: Bubble?) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value
        val x = b.x
        val y = b.y
        val r = b.rr
        var b1=ba
        while (b1 != null) {
            if (b1 !== b) {
                val x1 = b1.x
                val y1 = b1.y
                val r1 = b1.rr
                var d = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)
                if (d < (r1 + r) * (r1 + r)) {
                    d = sqrt(d)
//                    if(d==0f) d=1f
                    if (viewmodel.compress.value && d!=0f) {
                        var sz = d - b1.cr * 0.01f
                        var sz1 = d - b.cr * .01f
                        if (sz < 16f) sz = 16f
                        if (sz < b.cr) b.cr = sz
                        if (sz1 < 16f) sz1 = 16f
                        if (sz1 < b1.cr) b1.cr = sz1
                    }
                    val dx = if(d!=0f) (x1 - x)/d else Random.nextFloat()-0.5f
                    val dy = if(d!=0f) (y1 - y)/d else Random.nextFloat()-0.5f

                    val displacement = (r + r1) - d
                    b.sumdx += dx * displacement
                    b.sumdy += dy * displacement
                    b.count++
                    b1.vx = (b1.vx + rigidity * dx * displacement) * dampening
                    b1.vy = (b1.vy + rigidity * dy * displacement) * dampening
                }
            }
            b1 = b1.next
        }
    }

    fun update(dt:Float) {
        val dampening=viewmodel.dampening.value
        val rigidity=viewmodel.rigidity.value

        for(b in bubbleData) {
            if (!b.grabbed) {
                // move bubble
                var newx = b.x + b.vx * dt * 100
                var newy = b.y + b.vy * dt * 100
                b.x = clampWithAction(newx, b.cr, csize.width - b.cr-1) { b.vx *= -dampening }
                b.y = clampWithAction(newy, b.cr, csize.height - b.cr-1) { b.vy *= -dampening }
                b.vx += dt * mGravityX * 10
                b.vy += dt * mGravityY * 10
            }
            val x = b.x
            val y = b.y
            val r = b.rr
            b.cr = r
            b.sumdx=0f
            b.sumdy=0f
            b.count=0

            val ogx=b.gx
            val ogy=b.gy
            val gx=toindex(x,csize.width)
            val gy=toindex(y,csize.height)

            if(gx!=b.gx || gy!=b.gy) {
                b.gx=gx
                b.gy=gy
                // take it out of current list
                b.next?.prev = b.prev
                if (b.prev != null)
                    b.prev?.next = b.next
                else
                    bubblegrid[ogy][ogx] = b.next
                // add to new list
                b.next = bubblegrid[gy][gx]
                b.next?.prev = b
                b.prev = null
                bubblegrid[gy][gx] = b

            }
        }
        for (i in 0..(viewmodel.nbubbles.value - 1-viewmodel.nlarge.value) ) {
            val b = bubbleData[i]
            val r = b.rr+neighbourhood
            // broad phase, only look at small bubbles in neightbourhood
            for (yi in toindex(b.y -r, csize.height) .. toindex(b.y + r, csize.height)) {
                for (xi in toindex(b.x-r, csize.width)..toindex(b.x+r, csize.width)) {
                    narrowPhase(b, bubblegrid[yi][xi])
                }
            }
        }
        for (i in (viewmodel.nbubbles.value - 1-viewmodel.nlarge.value)..(viewmodel.nbubbles.value - 1))
            for(j in 0..(i - 1))
                narrowPhase(bubbleData[i],bubbleData[j])

        for (b in bubbleData ) {
            if(b.count!=0) {
                b.vx = (b.vx - rigidity * b.sumdx/(b.count)) * dampening
                b.vy = (b.vy - rigidity * b.sumdy/(b.count)) * dampening
            }
        }
    }

    fun grabBubble(x: Float, y: Float) {
        for(b in bubbleData) {
            if(b.inside(x,y)){
                b.grabbed=true
                b.vx=0f
                b.vy=0f
                grabbedBubble=b
                return
            }
        }
    }

    fun ungrabBubble() {
        grabbedBubble.let {
            it?.grabbed = false
            it?.vx = lastvx
            it?.vy = lastvy
        }
        return
    }

    fun moveBubble(x: Float, y: Float) {
        grabbedBubble?.let {
            lastvx = x - it.x
            lastvy = y - it.y
            it.x = x
            it.y = y
        }
    }

    fun init() {
        bubbleData = Array<Bubble>(viewmodel.nbubbles.value) { Bubble() }
        val nsmall=viewmodel.nbubbles.value-viewmodel.nlarge.value
        bubblegrid=Array(GRIDSIZE){Array(GRIDSIZE){null}}
        neighbourhood=viewmodel.smallMax.value.toFloat()
        for((i,b) in bubbleData.withIndex()) {
            with(b) {
              //  index=i
                vx = -5 + (0..100).random() / 10f
                vy = -5 + (0..100).random() / 10f
                val r = if (i < nsmall)
                    ((viewmodel.smallMin.value..viewmodel.smallMax.value).random()).toFloat()
                else
                    ((viewmodel.largeMin.value..viewmodel.largeMax.value).random()).toFloat()
                rr = r
                cr = r
                x = (r.toInt()..(csize.width - r).toInt()).random().toFloat()
                y = (r.toInt()..(csize.height - r).toInt()).random().toFloat()
                gx = toindex(x,csize.width)
                gy = toindex(y,csize.height)
                next=bubblegrid[gy][gx]
                next?.prev=this
                prev=null
                bubblegrid[gy][gx]=this
            }
        }
    }
}