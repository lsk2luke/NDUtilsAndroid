package com.nelepovds.ndutils.rest;

/**
 * Created by dmitrynelepov on 03.10.14.
 */
public interface IRestPagination {


    public void resetLoading();

    public Boolean loadMore();

    public void performLoading();

    public void updateUI(NDResultData resultData);
}
