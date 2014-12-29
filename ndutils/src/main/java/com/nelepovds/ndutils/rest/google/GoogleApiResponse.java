package com.nelepovds.ndutils.rest.google;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 28.12.14.
 */
public class GoogleApiResponse extends BaseClass {

    @SerializedName(value = "responseData")
    public GoogleApiResponseData responseData;

    @SerializedName(value = "responseDetails")
    public Object responseDetails;

    @SerializedName(value = "responseStatus")
    public Integer responseStatus;
}