package com.nelepovds.ndutils.rest.google;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 28.12.14.
 */
public class GoogleApiResponseData extends BaseClass {

    @SerializedName(value = "cursor")
    public GoogleApiCursor cursor;

    @SerializedName(value = "results")
    public ArrayList<GoogleApiImageInfo> results;
}
