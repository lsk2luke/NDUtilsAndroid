package com.nelepovds.ndutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.activeandroid.app.Application;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nelepovds.ndutils.common.Cache;
import com.nelepovds.ndutils.ui.ImageDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

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

    public String getPreferenceName() {
        return this.getClass().getSimpleName() + ".set";
    }


    public void runOnMainThread(Runnable runnable) {
        NDUtilsApplication.runOnMainThread(this, runnable);
    }

    public static void runOnMainThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(runnable);
    }

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(this.toString(), "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(this.toString(), "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences(this);
        int appVersion = getAppVersion(this);
        Log.i(this.toString(), "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(this.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static interface IGCMRegisterListener {

        public void registerSuccess(String regid);

        public void registerLoaded(String storedRegId);
    }

    public void registerDevice(final String senderId, final IGCMRegisterListener registerListener){
        String storedRegId = getRegistrationId(this);
        if (storedRegId.isEmpty()) {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String regid = gcm.register(senderId);
                        storeRegistrationId(regid);
                        registerListener.registerSuccess(regid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            registerListener.registerLoaded(storedRegId);
        }
    }
}

