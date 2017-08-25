package personal.yulie.android.httpgallery;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import personal.yulie.android.httpgallery.service.PollService;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = ExampleInstrumentedTest.class.getSimpleName();
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("personal.yulie.android.photogallery", appContext.getPackageName());
    }

    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void testWithStartedService() {
        Intent intent = PollService.newIntent(InstrumentationRegistry.getTargetContext());
        try {
            mServiceRule.startService(intent);
            Thread.sleep(100000);

        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServiceAlarm() {
        Context context = InstrumentationRegistry.getTargetContext();
        boolean shouldStartAlarm = !PollService.isServiceAlarmOn(context);
        PollService.setServiceAlarm(context, shouldStartAlarm);
        try {
            for (;;) {
                if (shouldStartAlarm) {
                    Log.i(TAG, Thread.currentThread() + ": testWithStartedService: Start service");
                    Thread.sleep(10000);
                } else {
                    Log.i(TAG, "testServiceAlarm: stop service");
                }
                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ActivityTestRule<PhotoGalleryActivity> mActivityRule =
            new ActivityTestRule<>(PhotoGalleryActivity.class);
    @Test
    public void testActivity() {
//        Log.i(TAG, "test: "+mActivityRule.getActivity());
    }
}
