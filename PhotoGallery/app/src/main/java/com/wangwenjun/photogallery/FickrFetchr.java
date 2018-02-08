package com.wangwenjun.photogallery;

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


/**
 * Created by Administrator on 2018/1/19.
 */

public class FickrFetchr {

    private static final String TAG = "FickrFetchr";
    private static final String SEARCH_WORD = "微距摄影";
    private static final Uri ENDPOINT = Uri.parse("http://image.baidu.com/search/index?")
            .buildUpon()
            //增加参数，这里指定结果以json格式返回
            .appendQueryParameter("tn", "resultjson")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                if(connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP||
                        connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {

                    String redirectUrl = connection.getHeaderField("Location");
                    if(redirectUrl !=null && redirectUrl.length() >0) {
                        connection = (HttpURLConnection) new URL(redirectUrl).openConnection();
                    }
                }else {
                    throw new IOException(connection.getResponseMessage() +
                            ": with " + urlSpec);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            //不断从网络下载数据，并保存到out中
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            //下载完毕后，将结果转化成byte array
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(null);
        return downloadGalleryItems(url);
    }
    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(query);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();
        try {

            String jsonString = getUrlString(url);
            Log.i(TAG, "Recevice JSON: " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }catch (JSONException e){
            Log.e(TAG, "Failed to parse JSON", e);
        }catch (IOException e) {
            Log.e(TAG, "Failed to fetch item", e);
        }

        return items;
    }

    private String buildUrl(String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        if (query != null) {
            //指定类别为微距摄影
            uriBuilder.appendQueryParameter("word", query);
        }else {
            uriBuilder.appendQueryParameter("word", SEARCH_WORD);
        }
        return uriBuilder.build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONArray photoJsonArray = jsonBody.getJSONArray("data");

        for(int i = 0; i < photoJsonArray.length(); i++){
            JSONObject  photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            if(!photoJsonObject.has("di")){
                continue;
            }
            item.setId(photoJsonObject.getString("di"));
            item.setCaption(photoJsonObject.getString("fromPageTitleEnc"));
            if(!photoJsonObject.has("objURL")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("objURL"));
            item.setfromURL(photoJsonObject.getString("fromURL"));
            items.add(item);
        }
    }

}
