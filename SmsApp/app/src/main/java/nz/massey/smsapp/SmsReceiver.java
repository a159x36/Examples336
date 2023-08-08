package nz.massey.smsapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SMS Receiver";
    public static final String pdu_type = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (Build.VERSION.SDK_INT >= 23)
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                 else
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Build the message to show.
                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

                NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                String CHANNEL_ID="my_channel";
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context,CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_mail_outline_black_24dp)
                                .setContentTitle("Mail")
                                .setContentText(strMessage);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My SMS App", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    nm.createNotificationChannel(notificationChannel);
                }
                nm.notify(0,mBuilder.build());
            }
        }
    }
}
