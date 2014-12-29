package com.nelepovds.ndutils.rest.google;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 28.12.14.
 */
public class GoogleApiImageInfo extends BaseClass {

    @SerializedName(value = "url")
    public String url;

    @SerializedName(value = "tbUrl")
    public String tbUrl;
}
