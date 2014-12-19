package com.nelepovds.ndutils.rest;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.Cache;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.nelepovds.ndutils.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Administrator on 14.12.14.
 */
public abstract class RestPaginationAdapter<T extends BaseClass> extends BaseAdapter implements IRestPagination {

    public static final String REST_PAGER_LIMIT = "limit";
    public static final String REST_PAGER_OFFSET = "offset";

    public static interface IRestPaginationAdapterListener {

        View getView(Object item, View convertView, ViewGroup parent);

        void beginLoading();

        void completeLoading();
    }

    protected NDRestBaseAPI restBaseAPI;

    protected Integer offset = 0;
    protected Integer limit = NDRestBaseAPI.ND_OFFSET_BASE_LIMIT;
    protected Integer total = 0;

    protected IRestPaginationAdapterListener restPaginationAdapterListener;

    protected HashMap<String, Object> getAdditionalParams() {
        HashMap<String, Object> retParams = new HashMap<>();
        retParams.put(REST_PAGER_OFFSET, this.offset);
        retParams.put(REST_PAGER_LIMIT, this.limit);
        return retParams;
    }

    protected abstract String getPathPagination();

    protected abstract Class getClassObject();

    protected Context context;

    protected Cursor resultCursor;

    public RestPaginationAdapter(NDRestBaseAPI restBaseAPI, Context context) {
        this.restBaseAPI = restBaseAPI;
        this.context = context;
        this.createCursor();
    }

    public void createCursor() {
        From query = new Select().from(getClassObject());
        HashMap<String, Object> params = this.getAdditionalParams();
        Iterator<String> keys = params.keySet().iterator();
        String[] sqlParams = new String[params.size() - 2];
        int index = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equalsIgnoreCase(REST_PAGER_LIMIT) && !key.equalsIgnoreCase(REST_PAGER_OFFSET)) {
                Object value = params.get(key);
                String paramKey = String.format("%s = ?", key);
                query = query.where(paramKey, value);
                sqlParams[index] = value.toString();
                index++;
            }
        }

        String sql = query.toSql();
        sql += String.format(" LIMIT %s,%s", String.valueOf(this.offset), String.valueOf(this.limit));
        this.resultCursor = Cache.openDatabase().rawQuery(sql, sqlParams);
    }

    @Override
    public int getCount() {
        return this.resultCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public T getItem(int position) {
        T item = null;
        this.resultCursor.moveToPosition(position);
        try {
            item = (T) getClassObject().newInstance();
            item.loadFromCursor(this.resultCursor);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (restPaginationAdapterListener != null) {
            return restPaginationAdapterListener.getView(this.getItem(position), convertView, parent);
        }
        TextView retDefaultView = new TextView(context);
        Object item = this.getItem(position);
        retDefaultView.setText(item.toString());
        int p = (int) context.getResources().getDimension(R.dimen.size8);
        retDefaultView.setPadding(p, p, p, p);
        return retDefaultView;
    }

    @Override
    public void resetLoading() {
        this.limit = NDRestBaseAPI.ND_OFFSET_BASE_LIMIT;
        this.offset = 0;

        this.performLoading();
    }

    @Override
    public Boolean loadMore() {
        if (this.offset + this.limit < this.total) {
            this.offset += this.limit;

            this.performLoading();
            return true;
        }

        return false;
    }

    @Override
    public void performLoading() {
        if (this.restPaginationAdapterListener != null) {
            this.restPaginationAdapterListener.beginLoading();
        }
        try {
            Method pagination = restBaseAPI.service.getClass().getMethod("pagination", new Class[]{String.class, HashMap.class, Callback.class});
            pagination.invoke(restBaseAPI.service, this.getPathPagination(), this.getAdditionalParams(), new Callback<NDResultData>() {
                @Override
                public void success(NDResultData resultData, Response response) {

                    if (resultData != null) {
                        for (Object object : resultData.data) {
                            T objClass = (T) BaseClass.fromJsonTreeMap(object, getClassObject(), new Select());
//                            parsedOneObject(objClass);
                        }
                    } else {
                        Log.wtf("Error", "Error");
                    }
                    updateUI(resultData);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    errorLoading(retrofitError);
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected abstract void parsedOneObject(T objectClass);

    @Override
    public void errorLoading(RetrofitError retrofitError) {
        Log.wtf("Error", "loading");
    }

    @Override
    public void updateUI(NDResultData resultData) {
        if (this.restPaginationAdapterListener != null) {
            this.restPaginationAdapterListener.completeLoading();
        }
        if (resultData != null) {
            this.offset = resultData.offset;
            this.total = resultData.total;
            this.limit = resultData.limit;
        }
        this.notifyDataSetChanged();
    }

    public void setRestPaginationAdapterListener(IRestPaginationAdapterListener restPaginationAdapterListener) {
        this.restPaginationAdapterListener = restPaginationAdapterListener;
    }
}
