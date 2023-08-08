package nz.massey.jobsworth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

public class MyIntentService extends IntentService {
    private static final String TAG = "MyIntentService";
    String CHANNEL_ID="my_channel";
    public MyIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Hi, I'm a Service");
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "My Service", importance);
            nm.createNotificationChannel(notificationChannel);
        }
        Notification.Builder mBuilder =
                new Notification.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_grade_24)
                        .setContentTitle("Service");
        for (int i = 0; i < 10; i++) {
            Log.i(TAG, "I'm a Service:"+ i);
            startForeground(1,mBuilder.setContentText("Number:"+i).build());
            SystemClock.sleep(1000);
        }
    }
}
