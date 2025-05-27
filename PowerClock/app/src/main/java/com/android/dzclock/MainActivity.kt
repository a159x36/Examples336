package com.android.dzclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.time.Clock
import java.time.LocalDateTime

private const val TAG = "PowerClock"

class MainActivity : ComponentActivity() {
    var hour by mutableIntStateOf(10)
    var minute by mutableIntStateOf(7)
    var temperature by mutableFloatStateOf(30f)
    var current by mutableFloatStateOf(-100f)
    var voltage by mutableFloatStateOf(3.4f)
    var avcurrent by mutableFloatStateOf(100f)
    var status by mutableIntStateOf(BatteryManager.BATTERY_STATUS_CHARGING)

    inner class PowerReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_BATTERY_CHANGED == action) {
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
                temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
                Log.i(TAG, "Voltage: $voltage")
            }
            val instant = Clock.systemDefaultZone().instant()
            val localTime = LocalDateTime.ofInstant(instant, Clock.systemDefaultZone().zone)
            minute = localTime.minute
            hour = localTime.hour + minute / 60
        }
    }
    lateinit var powerReceiver: PowerReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        powerReceiver = PowerReceiver()
        setContent {
            PowerClock()
        }
    }
    override fun onPause() {
        super.onPause()
        unregisterReceiver(powerReceiver)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(powerReceiver, filter)
    }

    @Preview
    @Composable
    fun PowerClock(modifier: Modifier = Modifier) {
        val dialsize=400
        val col=Color(0xFF3Fc115)
        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = modifier.fillMaxSize()) {
            val x = size.width / 2
            val y = size.height / 2
            for (i in 0..59) {
                var len = if (i % 5 == 0) 25 else 10
                rotate(degrees = i * 6f) {
                    drawLine(
                        start = Offset(x, (y - (dialsize / 2) + 40)),
                        end = Offset(x, (y - (dialsize / 2) + 40 + len)),
                        color = col,
                        strokeWidth = 4f
                    )
                }
            }
            rotate(degrees = hour * 30f) {
                drawLine(
                    start = Offset(x, (y - (dialsize / 2) + 90)),
                    end = Offset(x, y), color =col, strokeWidth = 12f
                )
            }
            rotate(degrees = minute * 6f) {
                drawLine(
                    start = Offset(x, (y - (dialsize / 2) + 68)),
                    end = Offset(x, y), color = col, strokeWidth = 6f
                )
            }
            drawCircle(
                center = Offset(x, y),
                radius = 10f,
                color =col
            )
            val fstr="%.2f"
            val cstr=fstr.format(-current / 1000)
            val pstr=fstr.format(-current * voltage / 1000)
            val s: String = if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
              "Charging: ${cstr}A · ${pstr}W · ${temperature}°C"
            } else "${current.toInt()}mA · Av ${avcurrent.toInt()}mA"
            val text=textMeasurer.measure(s, style = TextStyle(fontSize = 14.sp))
            drawText(text,color=col,  topLeft = Offset(x-text.size.width/2, (y + dialsize / 2 )))
        }
    }
}

