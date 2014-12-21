package com.nelepovds.ndutils.ui.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;

import com.nelepovds.ndutils.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NDRemoveAdMobActivity extends NDBillingActivity {

    public static final String ND_BILLING_REMOVE_ABMOD_PRODUCT_ID = "nd_billing_remove_admob_product_id";

    private String productId;
    private Integer requestId;


    public static void openActivity(Activity activity, Class classActivity, String productId, Integer requestId) {
        Intent intent = new Intent(activity, classActivity);
        intent.putExtra(ND_BILLING_REMOVE_ABMOD_PRODUCT_ID, productId);
        activity.startActivityForResult(intent, requestId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_remove_ad_mob);
        this.productId = getIntent().getStringExtra(ND_BILLING_REMOVE_ABMOD_PRODUCT_ID);
        this.initBilling();

    }

    protected void showDialogCheckPrev() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.title_ads);
        progressDialog.setMessage(getString(R.string.message_we_will_check_payments));
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle ownedItems = mBillingService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
                    int response = ownedItems.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> purchase_item_list = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                        if (purchase_item_list.contains(productId)) {
                            purchaseRemoveAd();
                        } else {
                            showNeedsBuy(progressDialog);
                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    showError();
                } catch (NullPointerException npe) {
                    progressDialog.dismiss();
                    showError();
                }
            }
        }).start();
    }

    protected void showNeedsBuy(ProgressDialog progressDialog) {
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(this.productId);

        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = mBillingService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    //Buy
                    if (sku.equalsIgnoreCase(productId)) {
                        Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(), sku, "inapp", null);
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                                requestId, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                Integer.valueOf(0));
                        break;
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();

    }

    protected void showError() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_error)
                .setTitle(R.string.message_error_purchases)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Восстановить покупки.
     */
    protected void purchaseRemoveAd() {
//        setResult(Activity.RESULT_OK);
//        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestId) {
            this.buyRemoveAdOk(resultCode, data);
        }
    }

    protected void buyRemoveAdOk(int resultCode, Intent data) {
        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

        if (resultCode == Activity.RESULT_OK) {
            try {
                JSONObject jo = new JSONObject(purchaseData);
                String sku = jo.getString("productId");
                purchaseRemoveAd();
            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_error)
                        .setTitle(R.string.message_error_purchases)
                        .show();
                e.printStackTrace();
            }
        }
    }
}
