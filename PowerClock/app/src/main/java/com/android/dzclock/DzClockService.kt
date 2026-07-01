package com.android.dzclock

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.service.dreams.DreamService
import android.util.Log
import androidx.annotation.CallSuper
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import java.time.Clock
import java.time.LocalDateTime

private const val TAG = "PowerClockService"
class DzClockService : DreamService(), SavedStateRegistryOwner, ViewModelStoreOwner {
    val dzClock = DzClock()
    inner class PowerReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received Service intent: ${intent.action}")
            val action = intent.action
            if (Intent.ACTION_BATTERY_CHANGED == action) {
                dzClock.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
                dzClock.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                dzClock.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
                Log.i(TAG, "Voltage: $dzClock.voltage")
            }
            dzClock.updateCurrent(this@DzClockService)
            val instant = Clock.systemDefaultZone().instant()
            val localTime = LocalDateTime.ofInstant(instant, Clock.systemDefaultZone().zone)
            dzClock.minute = localTime.minute
            dzClock.hour = localTime.hour + dzClock.minute / 60
        }
    }
    lateinit var powerReceiver: PowerReceiver
    @Suppress("LeakingThis")
	private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
	@Suppress("LeakingThis")
	private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
		performAttach()
	}
    @CallSuper
	override fun onCreate() {
		super.onCreate()
        powerReceiver = PowerReceiver()
		savedStateRegistryController.performRestore(null)
		lifecycleRegistry.currentState = Lifecycle.State.CREATED
	}

	override fun onDreamingStarted() {
		super.onDreamingStarted()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(powerReceiver, filter)
		lifecycleRegistry.currentState = Lifecycle.State.STARTED
	}

	override fun onDreamingStopped() {
		super.onDreamingStopped()
        unregisterReceiver(powerReceiver)
		lifecycleRegistry.currentState = Lifecycle.State.CREATED
	}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@DzClockService)
            setViewTreeViewModelStoreOwner(this@DzClockService)
            setViewTreeSavedStateRegistryOwner(this@DzClockService)
            setContent {
                dzClock.PowerClock()
            }
        }
        setContentView(composeView)
    }
}