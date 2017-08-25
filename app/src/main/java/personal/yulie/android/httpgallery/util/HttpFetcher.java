package personal.yulie.android.httpgallery.util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import personal.yulie.android.httpgallery.bean.GalleryItem;

/**
 * Created by android on 17-8-14.
 */

public class HttpFetcher {
    private static final String TAG = "HttpFetcher";
    private static final String HTTP_GANK_IO_API = "http://gank.io/api";
    private static final int BUFFER_SIZE = 1024;
    private static final Uri ENDPOINT = Uri.parse(HTTP_GANK_IO_API);

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        connection.getResponseMessage()
                        + ": with "
                        + urlSpec
                );

            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);
            }

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        byte[] urlBytes = getUrlBytes(urlSpec);
        return new String(urlBytes);
    }

    public List<GalleryItem> fetchItems(){
        Uri.Builder uriBuilder = Uri.parse(HTTP_GANK_IO_API).buildUpon();
        uriBuilder.appendEncodedPath("random/data/").appendPath("福利").appendPath("100");
        String url = uriBuilder.build().toString();
//        uriBuilder = Uri.parse(HTTP_GANK_IO_API).buildUpon();
//        uriBuilder.appendEncodedPath("random/data/福利").appendPath("10");
//        String url2 = uriBuilder.build().toString();
//        Log.i(TAG, "fetchItems: url1: " + url);
//        Log.i(TAG, "fetchItems: url2: " + url2);
        List<GalleryItem> items = new ArrayList<>();
        try {
            String jsonString = getUrlString(url);
//            Log.i(TAG, "fetchItems: fetched URL, received json " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItem(items, jsonBody);

        } catch (IOException e) {
            Log.e(TAG, "fetchItems: IOException", e);
        } catch (JSONException e) {
            Log.e(TAG, "fetchItems: JSONException", e);
        }
        return items;
    }

    private void parseItem(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONArray photoJsonArray = jsonBody.getJSONArray("results");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("_id"));
            item.setName(photoJsonObject.getString("who"));
            item.setUrl(photoJsonObject.getString("url"));
            items.add(item);
        }
    }
}
