package nz.ac.massey.examples336.touchbubbles

import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

const val TAG = "TouchBubbles"

class MainActivity : ComponentActivity(),SensorEventListener {

    var sensormanager:SensorManager?=null
    var accelerometer:Sensor?=null

    val viewmodel=SettingsViewModel()

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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
                Navigation(viewmodel)
        }
        sensormanager=getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer= sensormanager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }


    override fun onSensorChanged(p0: SensorEvent) {
        viewmodel.bubbles.setGravity(-p0.values[0],p0.values[1])
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    @Preview
    @Composable
    fun Test() {
        Navigation()
    }
}
