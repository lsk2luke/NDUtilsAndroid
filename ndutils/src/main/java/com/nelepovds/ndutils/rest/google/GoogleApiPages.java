package com.nelepovds.ndutils.rest.google;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 28.12.14.
 */
public class GoogleApiPages extends BaseClass {

    @SerializedName(value = "start")
    public String start;

    @SerializedName(value = "label")
    public Integer label;
}
