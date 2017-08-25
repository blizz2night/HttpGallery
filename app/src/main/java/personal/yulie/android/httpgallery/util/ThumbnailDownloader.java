package personal.yulie.android.httpgallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by android on 17-8-15.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentHashMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>{
        //target->PhotoHolder,fragment in main thread implements this method
        void onThumbnailDownload(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    String log = String.format("handleMessage: Thread ID: %d, Got a request for URL: %s",
                            getThreadId(),mRequestMap.get(target));
                    Log.i(TAG, log);
                    handleRequest(target);
                }
            }
        };
    }

    public void  clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);
        if (url == null) {
            return;
        }
        try {
            byte[] bitmapBytes = new HttpFetcher().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "handleRequest: Bitmap Created! Thread ID: "+getThreadId());
            //in mainThread
            //m.callback
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (mRequestMap.get(target) != url || mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    //update UI
                    mThumbnailDownloadListener.onThumbnailDownload(target, bitmap);
//                    Log.i(TAG, "UI updated,Thread id"+getThreadId());
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "handleRequest: Error downloading image", e);
        }
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueTumbnail(T target, String url) {
        String log = String.format("queueTumbnail: Thread ID: %d Got a URL: %s",getThreadId(),url);
        Log.i(TAG, log);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }
}
