package com.nelepovds.ndutils.ui.billing;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 15.10.14.
 */
public class NDPurchaseItem extends BaseClass {

    @SerializedName(value = "productId")
    public String productId;

    @SerializedName(value = "type")
    public String type;

    @SerializedName(value = "price")
    public String price;

    @SerializedName(value = "title")
    public String title;

    @SerializedName(value = "description")
    public String description;

}
