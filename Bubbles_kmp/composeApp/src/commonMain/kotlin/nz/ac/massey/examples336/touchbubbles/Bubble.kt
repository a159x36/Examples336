package nz.ac.massey.examples336.touchbubbles
data class Bubble(var x: Float = 0.0f,
                  var y: Float = 0.0f,
                  var vx: Float = 0.0f,
                  var vy: Float = 0.0f,
                  var rr: Float = 0.0f,
                  var cr: Float = 0.0f,
                  var gx: Int = 0,
                  var gy: Int = 0,
                  var sumdx: Float = 0f,
                  var sumdy: Float = 0f,
                  var count: Int = 0,
                  var next:Bubble? = null,
                  var prev:Bubble? = null,
                  var grabbed:Boolean=false) {
    fun inside(x1:Float, y1:Float): Boolean {
        return (x1 - x) * (x1 - x) +
                (y1 - y) * (y1 - y) <
                rr * rr
    }
}