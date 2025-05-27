package nz.ac.massey.gpxtrack

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.util.Locale

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Settings  {
    private object PreferenceKeys {
        val UNITS = stringPreferencesKey("units")
        val DISTANCE_UNITS = stringPreferencesKey("distance_units")
        val MAP_TYPE = stringPreferencesKey("map_type")
        val MAX_ACC = doublePreferencesKey("max_acc")
        val SHOW_ARROWS = booleanPreferencesKey("show_arrows")
        val SHOW_COLOURS = booleanPreferencesKey("show_colours")
        val SHOW_LEGEND = booleanPreferencesKey("show_legend")
        val SHOW_DETAILS = booleanPreferencesKey("show_details")
        val SHOW_MAX_SPEED = booleanPreferencesKey("show_max_speed")
        val LINE_WIDTH = intPreferencesKey("line_width")
        val WINDOW = intPreferencesKey("window")
        val CONVERT_TIME = booleanPreferencesKey("convert_time")
        val MIN_SPEED = doublePreferencesKey("min_speed")
    }
    private lateinit var app: Application

    data class AppSettings (
            val maxAcc: Double=0.0,
            val units: String="",
            val distanceUnits: String="",
            val mapType: String="",
            val showArrows: Boolean=false,
            val showColours: Boolean=false,
            val showLegend: Boolean=false,
            val showDetails: Boolean=false,
            val showMaxSpeed: Boolean=false,
            val lineWidth: Int=4,
            val window: Int=10,
            val wrongTz: Boolean=false,
            val minSpeed: Double=0.0
    )

    private val _settingsState=MutableStateFlow<AppSettings>(AppSettings())
    val settingsState= _settingsState.asStateFlow<AppSettings>()

    fun init(a: Application) {
        app = a
        val perfFlow = app.settingsDataStore.data
        .map { prefs ->
            val maxAcc=prefs[PreferenceKeys.MAX_ACC] ?: app.getString(R.string.max_acc_default).toDouble()
            val units=prefs[PreferenceKeys.UNITS] ?: app.getString(R.string.units_default)
            val distanceUnits=prefs[PreferenceKeys.DISTANCE_UNITS] ?: app.getString(R.string.distance_units_default)
            val mapType=prefs[PreferenceKeys.MAP_TYPE] ?: app.getString(R.string.map_type_default)
            val showArrows=prefs[PreferenceKeys.SHOW_ARROWS] ?: app.getString(R.string.show_arrows_default).toBoolean()
            val showColours=prefs[PreferenceKeys.SHOW_COLOURS] ?: app.getString(R.string.show_colours_default).toBoolean()
            val showLegend=prefs[PreferenceKeys.SHOW_LEGEND] ?: app.getString(R.string.show_legend_default).toBoolean()
            val showDetails=prefs[PreferenceKeys.SHOW_DETAILS] ?: app.getString(R.string.show_details_default).toBoolean()
            val showMaxSpeed=prefs[PreferenceKeys.SHOW_MAX_SPEED] ?: app.getString(R.string.show_max_speed_default).toBoolean()
            val lineWidth=prefs[PreferenceKeys.LINE_WIDTH] ?: app.resources.getInteger(R.integer.line_width_default)
            val window=prefs[PreferenceKeys.WINDOW] ?: app.resources.getInteger(R.integer.window_default)
            val wrongTz=prefs[PreferenceKeys.CONVERT_TIME] ?: app.getString(R.string.convert_time_default).toBoolean()
            val minSpeed=prefs[PreferenceKeys.MIN_SPEED] ?: app.getString(R.string.min_speed_default).toDouble()

            AppSettings(maxAcc=maxAcc, units=units, distanceUnits=distanceUnits,
                mapType=mapType, showArrows=showArrows, showColours=showColours,
                showLegend=showLegend, showDetails=showDetails, showMaxSpeed=showMaxSpeed,
                lineWidth=lineWidth, window=window, wrongTz=wrongTz, minSpeed=minSpeed)
        }
        CoroutineScope(Dispatchers.IO).launch {
            perfFlow.collect {
                _settingsState.update{currentstate->it}
            }
        }
    }

    fun isSettingsInitialized(): Boolean {
        return this::app.isInitialized
    }

    fun <T>setPref(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            app.settingsDataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    var units: String
        get() = _settingsState.value.units
        set(value) = setPref(PreferenceKeys.UNITS, value)

    var distanceUnits: String
        get() = _settingsState.value.distanceUnits
        set(value) = setPref(PreferenceKeys.DISTANCE_UNITS, value)

    var mapType: String
        get() = _settingsState.value.mapType
        set(value) = setPref(PreferenceKeys.MAP_TYPE, value)

    var maxAcc: Double
        get() = _settingsState.value.maxAcc
        set(value) = setPref(PreferenceKeys.MAX_ACC, value)

    var showArrows: Boolean
        get() = _settingsState.value.showArrows
        set(value) = setPref(PreferenceKeys.SHOW_ARROWS, value)

    var showColours: Boolean
        get() = _settingsState.value.showColours
        set(value) = setPref(PreferenceKeys.SHOW_COLOURS, value)

    var showLegend: Boolean
        get() = _settingsState.value.showLegend
        set(value) = setPref(PreferenceKeys.SHOW_LEGEND, value)

    var showDetails: Boolean
        get() = _settingsState.value.showDetails
        set(value) = setPref(PreferenceKeys.SHOW_DETAILS, value)

    var showMaxSpeed: Boolean
        get() = _settingsState.value.showMaxSpeed
        set(value) = setPref(PreferenceKeys.SHOW_MAX_SPEED, value)

    var lineWidth: Int
        get() = _settingsState.value.lineWidth
        set(value) = setPref(PreferenceKeys.LINE_WIDTH, value)

    var window: Int
        get() = _settingsState.value.window
        set(value) = setPref(PreferenceKeys.WINDOW, value)

    var wrongTz: Boolean
        get() = _settingsState.value.wrongTz
        set(value) = setPref(PreferenceKeys.CONVERT_TIME, value)

    var minSpeed: Double
        get() = _settingsState.value.minSpeed
        set(value) = setPref(PreferenceKeys.MIN_SPEED, value)


    fun getspeedandunits(sp: Double): String {
        val units = units
        return when (units) {
            "km/h" -> {
                String.format(Locale.getDefault(), "%.2f km/h", sp * 3600)
            }
            "knots" -> {
                String.format(Locale.getDefault(), "%.2f knots", sp * 1943.84449412)
            }
            else -> {
                String.format(Locale.getDefault(), "%.2f mph", sp * 2236.93629)
            }
        }
    }

    // d is in m
    fun getdistanceandunits(d: Double): String {
        val units = distanceUnits
        return when (units) {
            "km" -> {
                String.format(Locale.getDefault(), "%.2f km", d / 1000)
            }
            "nm" -> {
                String.format(Locale.getDefault(), "%.2f nm", d * 0.00053995680345572)
            }
            else -> {
                String.format(Locale.getDefault(), "%.2f miles", d * 0.00062137119223733)
            }
        }
    }

    fun getTime(starttime: DateTime): String? {
        val pattern = "MMM dd hh:mm a"
        return if (isSettingsInitialized() && wrongTz) DateTimeFormat.forPattern(
            pattern
        ).print(
            DateTime(starttime.millis, DateTimeZone.UTC)
        )
        else DateTimeFormat.forPattern(pattern).print(starttime)
    }
    fun getDesciptionString(
        starttime: Long,
        avspeed: Double,
        distance: Double,
        duration: Long,
        maxspeed: Double,
        max2secspeed: Double,
        foiltime: Double
    ): String {
        val time: String? = getTime(DateTime(starttime))
        val h = (duration / (60 * 60)).toInt()
        val m = (duration / 60).toInt() % 60
        return time + "\n1 Sec Max Speed: " + getspeedandunits(maxspeed) +
                "\n2 Sec Max Speed: " + getspeedandunits(max2secspeed) +
                "\nAverage Speed: " + getspeedandunits(avspeed) +
                "\nDistance: " + getdistanceandunits(distance) +
                "\nDuration: " + h + "h:" + String.format(Locale.getDefault(),
            "%02d",
            m
        ) + "m   " + String.format(Locale.getDefault(),"%2.2f", 100 * foiltime / duration) + "%"
    }

}
