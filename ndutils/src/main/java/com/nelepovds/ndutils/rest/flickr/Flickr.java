package com.nelepovds.ndutils.rest.flickr;

import android.app.Activity;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.nelepovds.ndutils.CommonUtils;
import com.nelepovds.ndutils.rest.RestApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 31.08.14.
 */
public class Flickr extends RestApi {

    public static final String FL_METHOD_PHOTOS_SEARCH = "?method=flickr.photos.search&api_key=%s&text=%s&format=json&nojsoncallback=1&per_page=%s&sort=relevance";
    public static final String FL_METHOD_PHOTOS_GET_SIZES = "?method=flickr.photos.getSizes&api_key=%s&photo_id=%s&format=json&nojsoncallback=1";


    private String flickrApiKey;

    public Flickr(String apiKey) {
        super("https://api.flickr.com/services/rest/", null, null);
        this.flickrApiKey = apiKey;
    }

    //https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=d42d6c8194f861d2d0fffc4cce3eb80b&text=puppy&per_page=20&format=json&nojsoncallback=1
    public void photosSearch(Activity activity, final String text, int per_page, IRestApiListener apiListener) {
        String apiMethod = String.format(FL_METHOD_PHOTOS_SEARCH, flickrApiKey, URLEncoder.encode(text), String.valueOf(per_page));
        this.apiCall(activity, apiMethod, HttpMethods.GET, null, null, NDFlickrApi.class, apiListener);
    }

    public static interface IFlickrSizesListener {

        public void sizes(NDFlickrPhoto photo, ArrayList<NDFlickrPhoto.NDFlickrPhotoSize> photoSizes);
    }

    public void getImageSizes(final NDFlickrPhoto photo, final IFlickrSizesListener listener) {
        final String apiMethod = String.format(FL_METHOD_PHOTOS_GET_SIZES, flickrApiKey, photo.photoId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NDFlickrPhoto.NDFlickrPhotoSize> photoSizes = new ArrayList<NDFlickrPhoto.NDFlickrPhotoSize>();
                try {
                    String retString = apiCall(apiMethod, HttpMethods.GET, null, null);
                    JSONObject jsonSize = new JSONObject(retString);
                    JSONObject sizes = jsonSize.getJSONObject("sizes");
                    JSONArray jsonSizes = sizes.getJSONArray("size");

                    for (int i = 0; i < jsonSizes.length(); i++) {
                        JSONObject oneSize = jsonSizes.getJSONObject(i);
                        NDFlickrPhoto.NDFlickrPhotoSize flickrPhotoSize = NDFlickrPhoto.NDFlickrPhotoSize.fromJson(oneSize.toString(), NDFlickrPhoto.NDFlickrPhotoSize.class);
                        photoSizes.add(flickrPhotoSize);
                    }
                    if (listener != null) {
                        listener.sizes(photo, photoSizes);
                    }
                    String ret = jsonSizes.toString();
                    Log.wtf("STR:", retString);
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (listener != null) {
                        listener.sizes(photo, photoSizes);
                    }
                }
            }
        }).start();

    }
}
