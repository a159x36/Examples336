package nz.ac.massey.examples336.touchbubbles

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.touchbubbles.theme.ui.AppTheme


const val TAG = "TouchBubbles"


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity(),SensorEventListener {

    lateinit var sensormanager:SensorManager
    var accelerometer:Sensor?=null
    lateinit var bubbles:Bubbles

    val viewmodel:SettingsViewModel by viewModels {
        SettingsViewModelFactory(dataStore) {
            CoroutineScope(Dispatchers.IO).launch {
                bubbles.init(this@MainActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(accelerometer!=null)
            sensormanager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        if(accelerometer!=null)
            sensormanager.unregisterListener(this)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("bubblemover")
        sensormanager=getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer=sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        bubbles=Bubbles(viewmodel)

        setContent {
            AppTheme {
                Navigation(viewmodel, bubbles)
            }
        }

        val sensors= sensormanager.getSensorList(Sensor.TYPE_ALL)
        for(sensor in sensors)
            Log.i(TAG,"Sensor: ${sensor.name}, type: ${sensor.type}")


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onSensorChanged(p0: SensorEvent) {
        bubbles.mGravityX=-p0.values[0]
        bubbles.mGravityY= p0.values[1]
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
   }


