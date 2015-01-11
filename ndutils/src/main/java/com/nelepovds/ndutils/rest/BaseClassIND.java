package com.nelepovds.ndutils.rest;

import com.activeandroid.annotation.Column;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitrynelepov on 11.01.15.
 */
public class BaseClassIND extends BaseClass {

    @Column(name = "name")
    @SerializedName(value = "name")
    public String name;

    @Column(name = "desc")
    @SerializedName(value = "desc")
    public String desc;
}
