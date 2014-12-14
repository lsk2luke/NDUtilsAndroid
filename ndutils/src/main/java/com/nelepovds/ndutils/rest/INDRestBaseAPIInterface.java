package com.nelepovds.ndutils.rest;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by Administrator on 14.12.14.
 */
public interface INDRestBaseAPIInterface {

    @GET("/{path_pagination}?offset={offset}&limit={limit}")
    void pagination(@Path("path_pagination") String pathPagination, @Path("offset") Integer offset, @Path("limit") Integer limit, @QueryMap HashMap<String, Object> params, Callback<NDResultData> result);
}
