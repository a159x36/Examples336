package nz.massey.jobsworth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

private const val TAG = "MyFgService"

class MyFgService : Service() {
    private val handler = Handler(Looper.getMainLooper())

    fun work() {
        Log.i(TAG, "I'm a Foreground Service, the time is:" + System.currentTimeMillis() / 1000)
        handler.postDelayed(::work, 5000)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        handler.post(::work)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "fg_service_channel"
        val channelName = "Foreground Service"

        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(channelName)
            .setContentText("Service is running...")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .build()
    }
}