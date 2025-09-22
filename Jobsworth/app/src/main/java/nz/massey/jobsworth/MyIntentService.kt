@file:Suppress("DEPRECATION")

package nz.massey.jobsworth

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Build
import android.os.SystemClock
import android.util.Log

private const val TAG = "MyIntentService"
private const val CHANNEL_ID: String = "my_channel"

class MyIntentService : IntentService(TAG) {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "Hi, I'm a Service")
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val importance = NotificationManager.IMPORTANCE_LOW
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "My Service", importance
        )
        nm.createNotificationChannel(notificationChannel)

        val mBuilder: Notification.Builder =
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_grade_24)
                .setContentTitle("Service")
        for (i in 0..9) {
            Log.i(TAG, "I'm a Service:$i")
            if(Build.VERSION.SDK_INT>=29)
                startForeground(1, mBuilder.setContentText("Number:$i").build(), FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            else
                startForeground(1, mBuilder.setContentText("Number:$i").build())
            SystemClock.sleep(1000)
        }
    }
}
