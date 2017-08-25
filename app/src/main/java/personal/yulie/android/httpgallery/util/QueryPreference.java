package personal.yulie.android.httpgallery.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by android on 17-8-25.
 */

public class QueryPreference {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static void setPrefLastResultId(Context context, String PREF_LAST_RESULT_ID) {

    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(PREF_IS_ALARM_ON, isOn)
        .apply();
    }
}
