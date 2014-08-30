package com.nelepovds.ndutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.activeandroid.app.Application;
import com.nelepovds.ndutils.common.Cache;
import com.nelepovds.ndutils.ui.ImageDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by dmitrynelepov on 08.06.14.
 */
public class NDUtilsApplication extends Application {

    public SharedPreferences sPref;

    public Cache cache;
    public ImageDownloader imageDownloader;
    public Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sPref = this.getSharedPreferences(getPreferenceName(), MODE_PRIVATE);
        this.cache = new Cache(getExternalCacheDir());
        this.imageDownloader = new ImageDownloader();
        this.picasso = Picasso.with(this);
        this.loadPreferences();
    }

    public void loadPreferences() {
    }

    public String getPreferenceName(){
        return this.getClass().getSimpleName()+".set";
    }



    public void runOnMainThread(Runnable runnable){
        NDUtilsApplication.runOnMainThread(this,runnable);
    }

    public static void runOnMainThread(Context context,Runnable runnable){
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(runnable);
    }
}

