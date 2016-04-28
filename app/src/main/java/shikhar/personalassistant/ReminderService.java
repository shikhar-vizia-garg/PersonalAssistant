package shikhar.personalassistant;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Shikhar Garg on 04-04-2016.
 */
public class ReminderService extends IntentService {
    private static final int NOTIF_ID = 1;
    DatabaseHelper dbhelper;
    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent notificationIntent = new Intent(this, Subjects.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);

        String content=intent.getStringExtra("Content").toString();
        int r=intent.getIntExtra("rno",-1);
        String title=intent.getStringExtra("Title").toString();
        dbhelper=new DatabaseHelper(getApplicationContext());
        dbhelper.deletereminder(r);
        //Log.i("content",content);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.icontodospeople)
                        .setContentTitle(title).setContentText(content);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
