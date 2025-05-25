package nz.ac.massey.examples336.touchbubbles

import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.touchbubbles.theme.ui.AppTheme


const val TAG = "TouchBubbles"

class MainActivity : ComponentActivity(),SensorEventListener {

    var sensormanager:SensorManager?=null
    var accelerometer:Sensor?=null

    lateinit var viewmodel:SettingsViewModel

    lateinit var bubbles:Bubbles

    override fun onResume() {
        super.onResume()
        sensormanager?.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensormanager?.unregisterListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("bubblemover")
        viewmodel=SettingsViewModel(application) {
            CoroutineScope(Dispatchers.IO).launch {
                bubbles.init(this@MainActivity)
            }
        }
        bubbles=Bubbles(viewmodel)

        setContent {
            AppTheme {
                Navigation(viewmodel, bubbles)
            }
        }
        sensormanager=getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer= sensormanager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onSensorChanged(p0: SensorEvent?) {
        bubbles.mGravityX=-p0!!.values[0]
        bubbles.mGravityY= p0.values[1]
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
   }


