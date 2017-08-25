package personal.yulie.android.httpgallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import personal.yulie.android.httpgallery.PhotoGalleryActivity;
import personal.yulie.android.httpgallery.R;
import personal.yulie.android.httpgallery.util.QueryPreference;


/**
 * Created by android on 17-8-24.
 */

public class PollService extends IntentService {

    public static final String ACTION_SHOW_NOFICATION = "personal.yulie.android.httpgallery.service.PollService.ACTION_SHOW_NOFICATION";
    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL = TimeUnit.MINUTES.toMillis(1);



    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreference.setAlarmOn(context, isOn);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
//        Log.i(TAG, Thread.currentThread()+": onHandleIntent: Received an intent: " + intent);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, Thread.currentThread()+": onHandleIntent: Received an intent: " + intent);
        Resources resources = getResources();
        if (!isNetworkAvailableAndConnected()) {
        }

        Intent i = PhotoGalleryActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_picture_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_picture_title))
                .setContentText(resources.getString(R.string.new_picture_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify((int) System.currentTimeMillis(),notification);
        sendBroadcast(new Intent(ACTION_SHOW_NOFICATION));
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //是否关闭后台下载
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        //网络是否已经连接
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
