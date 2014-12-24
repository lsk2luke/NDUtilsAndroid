package com.nelepovds.ndutils.ui.billing;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 15.10.14.
 */
public class NDPurchasedItem extends BaseClass {

    @SerializedName(value = "orderId")
    public String orderId;

    @SerializedName(value = "packageName")
    public String packageName;

    @SerializedName(value = "productId")
    public String productId;

    @SerializedName(value = "purchaseTime")
    public String purchaseTime;

    @SerializedName(value = "purchaseState")
    public String purchaseState;

    @SerializedName(value = "developerPayload")
    public String developerPayload;

    @SerializedName(value = "purchaseToken")
    public String purchaseToken;

    public static NDPurchasedItem fromPurchaseItem(NDPurchaseItem purchaseItem) {
        NDPurchasedItem retItem = null;
        if (purchaseItem != null) {
            retItem = new NDPurchasedItem();
            retItem.productId = purchaseItem.productId;
        }
        return retItem;
    }
}
