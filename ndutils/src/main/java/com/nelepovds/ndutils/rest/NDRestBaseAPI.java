package com.nelepovds.ndutils.rest;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.Locale;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Administrator on 28.11.14.
 */
public abstract class NDRestBaseAPI<T> {


    public static final Integer ND_OFFSET_BASE_LIMIT = 25;
    protected final RestAdapter adapter;
    public final T service;

    protected abstract String getWatcherID();

    protected String getLanguage() {
        return Locale.getDefault().getLanguage().toLowerCase();
    }


    public NDRestBaseAPI(String endPoint, Class<T> restInterfaceClass) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (getWatcherID() != null) {
                    request.addQueryParam("WatcherID", getWatcherID());
                }
                request.addQueryParam("AppOS", "Android");
                request.addQueryParam("Language", getLanguage());

            }
        };

        this.adapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    public void log(String msg) {
                        Log.i("retrofit", msg);
                    }
                })
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(BaseClass.gsonAdapter()))

                .setClient(new OkClient(new OkHttpClient()))
                .build();
        this.service = this.adapter.create(restInterfaceClass);
    }


}
