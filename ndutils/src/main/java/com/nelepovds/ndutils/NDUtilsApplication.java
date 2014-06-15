package com.nelepovds.ndutils;

import android.content.SharedPreferences;

import com.activeandroid.app.Application;

/**
 * Created by dmitrynelepov on 08.06.14.
 */
public class NDUtilsApplication extends Application {

    public SharedPreferences sPref;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sPref = this.getSharedPreferences(getPreferenceName(), MODE_PRIVATE);
        this.loadPreferences();
    }

    public void loadPreferences() {
    }

    public String getPreferenceName(){
        return this.getClass().getSimpleName()+".set";
    }



}

