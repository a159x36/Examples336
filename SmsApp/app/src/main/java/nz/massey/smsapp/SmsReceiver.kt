package nz.massey.smsapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

private const val TAG = "SMS Receiver"
private const val FORMAT_TYPE: String = "format"
private const val PDU_TYPE: String = "pdus"

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val msgs: Array<SmsMessage?>
        var strMessage = ""
        val format = intent.getStringExtra(FORMAT_TYPE)
        // Retrieve the SMS message received.
        val pdus:Array<ByteArray>? = (if(Build.VERSION.SDK_INT>=33) {
            intent.getSerializableExtra(PDU_TYPE, Array<ByteArray>::class.java)
        } else {
            @Suppress("DEPRECATION")
            val extra=intent.getSerializableExtra(PDU_TYPE);
            extra as? Array<ByteArray>
        })

        if (pdus != null) {
            // Fill the msgs array.
            msgs = arrayOfNulls(pdus.size)
            for (i in msgs.indices) {
                // Check Android version and use appropriate createFromPdu.
                msgs[i] = SmsMessage.createFromPdu(pdus[i], format)
                // Build the message to show.
                strMessage += "SMS from " + msgs[i]?.getOriginatingAddress()
                strMessage += " :" + msgs[i]?.getMessageBody() + "\n"
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
