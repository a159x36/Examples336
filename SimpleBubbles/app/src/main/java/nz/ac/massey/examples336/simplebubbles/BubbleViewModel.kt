package nz.ac.massey.examples336.simplebubbles

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel

class BubbleViewModel(context: Context): ViewModel() {
    val FPS=60L
    var frameNumber = mutableIntStateOf(1)
    var canvasSize by mutableStateOf(Size(0f,0f))
    data class Bubble(var x:Float, var y:Float, var vx:Float, var vy:Float, var r:Float=100f, var color:Color)

    val NBUBBLES=100
    val bubbleImage: ImageBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.bubble).asImageBitmap()

    init {
        init()
    }

    lateinit var bubbles:MutableList<Bubble>

    var mGravityX = 0f
    var mGravityY = 0f

    fun clampWithAction(value: Float, min: Float, max: Float, run: () -> Unit = {}): Float {
        var newvalue = value
        if (value < min) {
            newvalue = min
            run()
        }
        if (value > max) {
            newvalue = max
            run()
        }
        return newvalue
    }

    fun update(dt: Float, size:Size) {
        val dampening = 1.0f

        for (b in bubbles) {
            var newx = b.x + b.vx * dt * 100
            var newy = b.y + b.vy * dt * 100
            b.x = clampWithAction(newx, b.r, size.width - b.r) {
                b.vx *= -dampening
            }
            b.y = clampWithAction(newy, b.r, size.height - b.r) {
                b.vy *= -dampening
            }
            b.vx += dt * mGravityX * 10
            b.vy += dt * mGravityY * 10
        }
    }

    fun init(size:Size=Size(500f,500f)) {
        bubbles=MutableList<Bubble>(NBUBBLES){
            val r=(50..200).random()
            Bubble( (r..((size.width-r).toInt())).random().toFloat(),
                (r..((size.height-r).toInt())).random().toFloat(),
                (-50..50).random()/10f,
                (-50..50).random()/10f,
                r.toFloat(),
                Color((64..255).random(),(64..255).random(),(64..255).random())
            )
        }
    }
}