package com.android.dzclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "DzClock"

@Composable
fun Main(clock: PowerClock,    modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                clock
            },
        )
    }
}


class MainActivity : AppCompatActivity() {
    private lateinit var mClock: PowerClock

    lateinit var mPcr: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        mClock=PowerClock(this,null)
        super.onCreate(savedInstanceState)
        setContent {
            Main(mClock)
        }

        mPcr = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (Intent.ACTION_BATTERY_CHANGED == action) {
                    val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    mClock.setVoltage(voltage / 1000.0f)
                    val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    mClock.setTemperature(temp / 10.0f)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
                    mClock.setStatus(status)
                    Log.i(TAG, "Voltage: $voltage")
                }
                mClock.onTimeChanged()
            }
        }
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mPcr, filter)
    }

}