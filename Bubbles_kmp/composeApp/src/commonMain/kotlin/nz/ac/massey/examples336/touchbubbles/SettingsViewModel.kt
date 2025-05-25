package nz.ac.massey.examples336.touchbubbles


import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



class SettingsViewModel()  : ViewModel() {
    private val _nlarge = MutableStateFlow(1)
    private val _nbubbles = MutableStateFlow(100)
    private val _fps = MutableStateFlow(60)
    private val _compress = MutableStateFlow(true)
    private val _dampening = MutableStateFlow(0.9f)
    private val _rigidity = MutableStateFlow(0.3f)
    private val _smallMin = MutableStateFlow(40)
    private val _smallMax = MutableStateFlow(50)
    private val _largeMin = MutableStateFlow(200)
    private val _largeMax = MutableStateFlow(300)

    fun prefschanged() {
        bubbles.init()
    }

    val nbubbles=_nbubbles.asStateFlow()
    val nlarge=_nlarge.asStateFlow()
    val fps=_fps.asStateFlow()
    val compress=_compress.asStateFlow()
    val dampening=_dampening.asStateFlow()
    val rigidity=_rigidity.asStateFlow()
    val smallMin=_smallMin.asStateFlow()
    val smallMax=_smallMax.asStateFlow()
    val largeMin=_largeMin.asStateFlow()
    val largeMax=_largeMax.asStateFlow()

    fun setnbubbles(n:Int) {_nbubbles.value=n;prefschanged()}
    fun setnlarge(n:Int) {_nlarge.value=n ;prefschanged()}

    fun setfps(n:Int) {_fps.value=n;prefschanged()}
    fun setcompress(n:Boolean) {_compress.value=n;prefschanged()}
    fun setdampening(n:Float) {_dampening.value=n;prefschanged()}
    fun setrigidity(n:Float) {_rigidity.value=n;prefschanged()}
    fun setsmall_min(n:Int) {_smallMin.value=n;prefschanged()}
    fun setsmall_max(n:Int) {_smallMax.value=n;prefschanged()}
    fun setlarge_min(n:Int) {_largeMin.value=n;prefschanged()}
    fun setlarge_max(n:Int)  {_largeMax.value=n;prefschanged()}

    val bubbles=Bubbles(this)
}
