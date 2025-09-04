package nz.ac.massey.examples336.touchbubbles

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(val dataStore: DataStore<Preferences>, val prefschanged: () -> Unit) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(dataStore, prefschanged) as T
    }
}
class SettingsViewModel(val dataStore: DataStore<Preferences>, prefschanged: () -> Unit)  : ViewModel() {
    private val _nlarge = MutableStateFlow(0)
    private val _nbubbles = MutableStateFlow(0)
    private val _fps = MutableStateFlow(0)
    private val _compress = MutableStateFlow(true)
    private val _dampening = MutableStateFlow(0f)
    private val _rigidity = MutableStateFlow(0f)
    private val _smallMin = MutableStateFlow(0)
    private val _smallMax = MutableStateFlow(0)
    private val _largeMin = MutableStateFlow(0)
    private val _largeMax = MutableStateFlow(0)
    private val _native = MutableStateFlow(false)
    private val _usedirect = MutableStateFlow(false)

    private object PreferenceKeys {
        val NLARGE=intPreferencesKey("nlarge")
        val NBUBBLES=intPreferencesKey("nbubbles")
        val FPS=intPreferencesKey("fps")
        val COMPRESS=booleanPreferencesKey("compress")
        val DAMPENING=floatPreferencesKey("dampening")
        val RIGIDITY=floatPreferencesKey("rigidity")
        val SMALL_MIN=intPreferencesKey("small_min")
        val SMALL_MAX=intPreferencesKey("small_max")
        val LARGE_MIN=intPreferencesKey("large_min")
        val LARGE_MAX=intPreferencesKey("large_max")
        val NATIVE=booleanPreferencesKey("native")
        val USEDIRECT=booleanPreferencesKey("usedirect")
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect {
                preferences ->
                _nbubbles.value=preferences[PreferenceKeys.NBUBBLES]?:200
                _nlarge.value=preferences[PreferenceKeys.NLARGE]?:1
                _fps.value=preferences[PreferenceKeys.FPS]?:60
                _compress.value=preferences[PreferenceKeys.COMPRESS]?:true
                _dampening.value=preferences[PreferenceKeys.DAMPENING]?:0.9f
                _rigidity.value=preferences[PreferenceKeys.RIGIDITY]?:0.3f
                _smallMin.value=preferences[PreferenceKeys.SMALL_MIN]?:20
                _smallMax.value=preferences[PreferenceKeys.SMALL_MAX]?:30
                _largeMin.value=preferences[PreferenceKeys.LARGE_MIN]?:100
                _largeMax.value=preferences[PreferenceKeys.LARGE_MAX]?:200
                _native.value=preferences[PreferenceKeys.NATIVE]?:false
                _usedirect.value=preferences[PreferenceKeys.USEDIRECT]?:false
                prefschanged()
            }
        }
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
    val native=_native.asStateFlow()
    val usedirect=_usedirect.asStateFlow()

    fun <T>setPref(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    fun setNBubbles(n:Int) {setPref(PreferenceKeys.NBUBBLES,n) }
    fun setNLarge(n:Int) {setPref(PreferenceKeys.NLARGE,n) }
    fun setFps(n:Int) {setPref(PreferenceKeys.FPS,n) }
    fun setCompress(n:Boolean) {setPref(PreferenceKeys.COMPRESS,n) }
    fun setDampening(n:Float) {setPref(PreferenceKeys.DAMPENING,n) }
    fun setRigidity(n:Float) {setPref(PreferenceKeys.RIGIDITY,n) }
    fun setSmallMin(n:Int) {setPref(PreferenceKeys.SMALL_MIN,n) }
    fun setSmallMax(n:Int) {setPref(PreferenceKeys.SMALL_MAX,n) }
    fun setlargeMin(n:Int) {setPref(PreferenceKeys.LARGE_MIN,n) }
    fun setLargeMax(n:Int)  {setPref(PreferenceKeys.LARGE_MAX,n) }
    fun setNative(n:Boolean) {setPref(PreferenceKeys.NATIVE,n) }
    fun setUseDirect(n:Boolean) {setPref(PreferenceKeys.USEDIRECT,n) }
}
