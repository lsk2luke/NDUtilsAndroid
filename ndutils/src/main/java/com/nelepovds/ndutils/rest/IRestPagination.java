package com.nelepovds.ndutils.rest;

import retrofit.RetrofitError;

/**
 * Created by dmitrynelepov on 03.10.14.
 */
public interface IRestPagination {


    public void resetLoading();

    public Boolean loadMore();

    public void performLoading();

    public void updateUI(NDResultData resultData);

    public void errorLoading(RetrofitError retrofitError);
}
