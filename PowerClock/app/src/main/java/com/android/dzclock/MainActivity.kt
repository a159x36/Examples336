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

import java.time.Clock
import java.time.LocalDateTime

private const val TAG = "PowerClock"

class MainActivity : ComponentActivity() {
    val dzClock = DzClock()
    inner class PowerReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_BATTERY_CHANGED == action) {
                dzClock.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
                dzClock.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                dzClock.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
                Log.i(TAG, "Voltage: $dzClock.voltage")
            }
            dzClock.updateCurrent(this@MainActivity)
            val instant = Clock.systemDefaultZone().instant()
            val localTime = LocalDateTime.ofInstant(instant, Clock.systemDefaultZone().zone)
            dzClock.minute = localTime.minute
            dzClock.hour = localTime.hour + dzClock.minute / 60
        }
    }
    lateinit var powerReceiver: PowerReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        powerReceiver = PowerReceiver()
        setContent {
            dzClock.PowerClock()
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


}

