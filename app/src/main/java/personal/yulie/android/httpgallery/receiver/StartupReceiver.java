package personal.yulie.android.httpgallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import personal.yulie.android.httpgallery.service.PollService;
import personal.yulie.android.httpgallery.util.QueryPreference;

/**
 * Created by android on 17-8-25.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: Receive broadcast intent: " + intent.getAction()+Thread.currentThread());

        boolean isOn = QueryPreference.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);
    }
}
