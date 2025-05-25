package nz.ac.massey.examples336.touchbubbles

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class SettingsViewModel(private val app: Application, prefschanged: () -> Unit)  : ViewModel() {
    private val _nlarge = MutableStateFlow(0)
    private val _nbubbles = MutableStateFlow(0)
    private val _fps = MutableStateFlow(0)
    private val _compress = MutableStateFlow(true)
    private val _dampening = MutableStateFlow(0f)
    private val _rigidity = MutableStateFlow(0f)
    private val _small_min = MutableStateFlow(0)
    private val _small_max = MutableStateFlow(0)
    private val _large_min = MutableStateFlow(0)
    private val _large_max = MutableStateFlow(0)
    private val _native = MutableStateFlow(false)

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
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.data.collect {
                preferences ->
                _nbubbles.value=preferences[PreferenceKeys.NBUBBLES]?:200
                _nlarge.value=preferences[PreferenceKeys.NLARGE]?:1
                _fps.value=preferences[PreferenceKeys.FPS]?:60
                _compress.value=preferences[PreferenceKeys.COMPRESS]?:true
                _dampening.value=preferences[PreferenceKeys.DAMPENING]?:0.9f
                _rigidity.value=preferences[PreferenceKeys.RIGIDITY]?:0.3f
                _small_min.value=preferences[PreferenceKeys.SMALL_MIN]?:20
                _small_max.value=preferences[PreferenceKeys.SMALL_MAX]?:30
                _large_min.value=preferences[PreferenceKeys.LARGE_MIN]?:100
                _large_max.value=preferences[PreferenceKeys.LARGE_MAX]?:200
                _native.value=preferences[PreferenceKeys.NATIVE]?:false
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
    val small_min=_small_min.asStateFlow()
    val small_max=_small_max.asStateFlow()
    val large_min=_large_min.asStateFlow()
    val large_max=_large_max.asStateFlow()
    val native=_native.asStateFlow()

    fun <T>setPref(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    fun setnbubbles(n:Int) {setPref(PreferenceKeys.NBUBBLES,n) }
    fun setnlarge(n:Int) {setPref(PreferenceKeys.NLARGE,n) }
    fun setfps(n:Int) {setPref(PreferenceKeys.FPS,n) }
    fun setcompress(n:Boolean) {setPref(PreferenceKeys.COMPRESS,n) }
    fun setdampening(n:Float) {setPref(PreferenceKeys.DAMPENING,n) }
    fun setrigidity(n:Float) {setPref(PreferenceKeys.RIGIDITY,n) }
    fun setsmall_min(n:Int) {setPref(PreferenceKeys.SMALL_MIN,n) }
    fun setsmall_max(n:Int) {setPref(PreferenceKeys.SMALL_MAX,n) }
    fun setlarge_min(n:Int) {setPref(PreferenceKeys.LARGE_MIN,n) }
    fun setlarge_max(n:Int)  {setPref(PreferenceKeys.LARGE_MAX,n) }
    fun setnative(n:Boolean) {setPref(PreferenceKeys.NATIVE,n) }
}
