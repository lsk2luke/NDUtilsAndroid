package com.nelepovds.ndutils.rest.google;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 28.12.14.
 */
public class GoogleApiCursor extends BaseClass {

    @SerializedName(value = "resultCount")
    public String resultCount;

    @SerializedName(value = "estimatedResultCount")
    public String estimatedResultCount;

    @SerializedName(value = "currentPageIndex")
    public Integer currentPageIndex;

    @SerializedName(value = "moreResultsUrl")
    public String moreResultsUrl;

    @SerializedName(value = "searchResultTime")
    public Double searchResultTime;

    @SerializedName(value = "pages")
    public ArrayList<GoogleApiPages> pages;
}
