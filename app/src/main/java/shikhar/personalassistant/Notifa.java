package shikhar.personalassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Shikhar Garg on 04-04-2016.
 */
public class Notifa extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        // Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        showNotification(context);
    }
    private void showNotification(Context context) {
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
          //      new Intent(context, Login.class), 0);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icontodospeople)
                        .setContentTitle(context.getString(R.string.app_name));
                        //.setContentText(context.getString(R.string.testo_notifica));
        //mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
