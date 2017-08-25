package personal.yulie.android.httpgallery.util;

import android.os.AsyncTask;

import java.util.List;

import personal.yulie.android.httpgallery.bean.GalleryItem;

/**
 * Created by android on 17-8-14.
 */

public class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
    private static final String TAG = "FetchItemsTask";
    private static final String HTTP_GANK_IO = "http://gank.io/";

    private TaskCompleteListener mTaskCompleteListener;

    public interface TaskCompleteListener {
        void onTaskCompleted(List<GalleryItem> galleryItems);
    }

    public void setTaskCompleteListener(TaskCompleteListener taskCompleteListener) {
        mTaskCompleteListener = taskCompleteListener;
    }

    public FetchItemsTask(){
        super();
    }

    public FetchItemsTask(TaskCompleteListener taskCompleteListener){
        this();
        mTaskCompleteListener = taskCompleteListener;
    }
    @Override
    protected List<GalleryItem> doInBackground(Void... params) {
//        try {
//            String result = new HttpFetcher().getUrlString(HTTP_GANK_IO);
//            Log.i(TAG, "doInBackground: Fetched content of URL: \n" + result);
//        } catch (IOException e) {
//            Log.e(TAG, "doInBackground: ", e);
//        }
        return new HttpFetcher().fetchItems();
    }

    @Override
    protected void onPostExecute(List<GalleryItem> galleryItems) {
        super.onPostExecute(galleryItems);
        if (mTaskCompleteListener != null) {
            mTaskCompleteListener.onTaskCompleted(galleryItems);
        }
    }
}
