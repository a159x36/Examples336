package nz.massey.smsapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

private const val TAG = "SMS Receiver"

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == android.provider.Telephony.Sms.Intents
            .SMS_RECEIVED_ACTION) {
            val messages = getMessagesFromIntent(intent)
            for (msg in messages) {
                // Build the message to show.
                val strMessage =
                    "SMS from ${msg.originatingAddress} " +
                    ": ${msg.messageBody}\n"
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: $strMessage")
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show()

                val nm =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val CHANNEL_ID = "my_channel"
                val notificationBuilder =
                    NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_mail_outline_black_24dp)
                        .setContentTitle("SMS Message")
                        .setContentText(strMessage)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val notificationChannel =
                        NotificationChannel(CHANNEL_ID, "My SMS App", importance)
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.enableVibration(true)
                    notificationChannel.vibrationPattern =
                        longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                    nm.createNotificationChannel(notificationChannel)
                }
                nm.notify(0, notificationBuilder.build())
            }
        }
    }
}
