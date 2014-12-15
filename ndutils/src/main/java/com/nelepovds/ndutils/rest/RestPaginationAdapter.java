package com.nelepovds.ndutils.rest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nelepovds.ndutils.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Administrator on 14.12.14.
 */
public abstract class RestPaginationAdapter<T extends BaseClass> extends BaseAdapter implements IRestPagination {

    protected NDRestBaseAPI restBaseAPI;

    protected Integer offset = 0;
    protected Integer limit = NDRestBaseAPI.ND_OFFSET_BASE_LIMIT;
    protected Integer total = 0;


    protected HashMap<String, Object> getAdditionalParams() {
        HashMap<String, Object> retParams = new HashMap<>();
        retParams.put("offset", this.offset);
        retParams.put("limit", this.limit);
        return retParams;
    }

    protected abstract String getPathPagination();

    protected abstract Class getClassObject();

    protected Context context;


    public RestPaginationAdapter(NDRestBaseAPI restBaseAPI, Context context) {
        this.restBaseAPI = restBaseAPI;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        try {
            Method pagination = restBaseAPI.service.getClass().getMethod("pagination", new Class[]{String.class, HashMap.class, Callback.class});
            pagination.invoke(restBaseAPI.service, this.getPathPagination(), this.getAdditionalParams(), new Callback<NDResultData>() {
                @Override
                public void success(NDResultData resultData, Response response) {

                    if (resultData != null) {
                        for (Object object : resultData.data) {
                            T objClass = (T) BaseClass.fromJsonTreeMap(object, getClassObject(), new Select());
                            parsedOneObject(objClass);
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
        this.offset = resultData.offset;
        this.total = resultData.total;
        this.limit = resultData.limit;
        this.notifyDataSetChanged();
//        this.setNumColumns(gridViewArtWorksAdapter.getCount() <= 3 ? 1 : 3);
//        gridViewArtWorksAdapter.notifyDataSetChanged();
//        setShowLoading(false);
//        Boolean hasMore = this.offset + this.limit < this.total;
//        setShowMore(hasMore);
//        if (this.listener != null) {
//            this.listener.completePartOfDataLoading(hasMore, gridViewArtWorksAdapter.getCount(), resultData.total);
//        }
    }
}
