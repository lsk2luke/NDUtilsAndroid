package com.nelepovds.ndutils.rest.flickr;

import android.app.Activity;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.nelepovds.ndutils.CommonUtils;
import com.nelepovds.ndutils.rest.RestApi;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 31.08.14.
 */
public class Flickr extends RestApi {

    public static final String FL_METHOD_PHOTOS_SEARCH = "?method=flickr.photos.search&api_key=%s&text=%s&per_page=20&format=json&nojsoncallback=1&per_page=%s&sort=relevance";


    private String flickrApiKey;

    public Flickr(String apiKey) {
        super("https://api.flickr.com/services/rest/", null, null);
        this.flickrApiKey = apiKey;
    }

    //https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=d42d6c8194f861d2d0fffc4cce3eb80b&text=puppy&per_page=20&format=json&nojsoncallback=1
    public void photosSearch(Activity activity, final String text, int per_page, IRestApiListener apiListener){
        String apiMethod = String.format(FL_METHOD_PHOTOS_SEARCH,flickrApiKey,URLEncoder.encode(text),String.valueOf(per_page));
        this.apiCall(activity,apiMethod,HttpMethods.GET,null,null,NDFlickrApi.class,apiListener);
    }
}
