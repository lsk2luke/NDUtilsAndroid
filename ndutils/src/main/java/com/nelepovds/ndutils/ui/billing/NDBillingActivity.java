package com.nelepovds.ndutils.ui.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.nelepovds.ndutils.R;
import com.nelepovds.ndutils.ui.NDActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 21.12.14.
 */
public class NDBillingActivity extends NDActivity {

    private static final int ND_PURCHASE_ITEM_REQUEST = 1001;
    protected ServiceConnection mBillingServiceConn;
    protected IInAppBillingService mBillingService;

    protected NDPurchaseItem currentBuyItem;

    @Override
    protected void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState, layoutId);
        initBilling();
    }

    protected void initBilling() {
        //Billing
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        this.mBillingServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBillingServiceConn = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                setBillingService(IInAppBillingService.Stub.asInterface(service));
            }
        };
        getActivity().bindService(serviceIntent, mBillingServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void setBillingService(IInAppBillingService mBillingService) {
        this.mBillingService = mBillingService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBillingService != null) {
            this.unbindService(this.mBillingServiceConn);
        }
    }

    /**
     * Загрузка информации о продуктах
     *
     * @param skuList - список идентификаторов продуктов
     * @return
     */
    protected ArrayList<NDPurchaseItem> loadPurchasableItems(final ArrayList<String> skuList) {
        this.showProgressDialog(R.string.dialog_purchases, R.string.dialog_loading_purchases);
        ArrayList<NDPurchaseItem> items = new ArrayList<>();
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = mBillingService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");
                for (String thisResponse : responseList) {
                    NDPurchaseItem purchaseItem = NDPurchaseItem.fromJson(thisResponse, NDPurchaseItem.class);
                    items.add(purchaseItem);
                }

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Покупка
     *
     * @param purchaseItem
     */
    protected void buyItem(NDPurchaseItem purchaseItem) {
        this.showProgressDialog(getString(R.string.title_purchasing), purchaseItem.title);
        this.currentBuyItem = purchaseItem;
        Bundle buyIntentBundle = null;
        try {
            buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(), purchaseItem.productId, "inapp", null);
            Integer resultCode = buyIntentBundle.getInt("RESPONSE_CODE");
            if (resultCode == 0) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                this.startIntentSenderForResult(pendingIntent.getIntentSender(),
                        ND_PURCHASE_ITEM_REQUEST, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } else {
                this.consumePurchase(NDPurchasedItem.fromPurchaseItem(purchaseItem), this.currentBuyItem);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NDBillingActivity.ND_PURCHASE_ITEM_REQUEST) {
                this.buyComplete(data);
            }
        }
    }

    protected void buyComplete(Intent data) {
        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

        try {
            NDPurchasedItem purchasedItem = NDPurchasedItem.fromJson(purchaseData, NDPurchasedItem.class);
            consumePurchase(purchasedItem, null);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_error)
                    .setTitle(R.string.message_error_purchases)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * Подтверждить покупку
     *
     * @param purchasedItem
     * @param purchaseItem
     */
    protected void consumePurchase(NDPurchasedItem purchasedItem, NDPurchaseItem purchaseItem) {
        String purchaseToken = "inapp:" + this.getPackageName() + ":" + purchasedItem.productId;
        try {
            mBillingService.consumePurchase(3, this.getPackageName(), purchaseToken);
            hideProgressDialog();

            productBuyComplete(purchasedItem, purchaseItem == null ? this.currentBuyItem : purchaseItem);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    protected void productBuyComplete(NDPurchasedItem purchasedItem, NDPurchaseItem purchaseItem) {

    }
}
