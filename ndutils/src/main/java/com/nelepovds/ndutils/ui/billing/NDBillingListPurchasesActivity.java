package com.nelepovds.ndutils.ui.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.vending.billing.IInAppBillingService;
import com.nelepovds.ndutils.R;

import java.util.ArrayList;

public abstract class NDBillingListPurchasesActivity extends NDBillingActivity {

    protected ListView listViewPurchases;

    protected ArrayList<String> productsIds;
    protected NDPurchasesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.listViewPurchases = new ListView(this);
        this.setContentView(this.listViewPurchases);
        this.initBilling();
        this.adapter = new NDPurchasesAdapter();
        this.listViewPurchases.setAdapter(this.adapter);
        this.listViewPurchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NDPurchaseItem purchaseItem = (NDPurchaseItem) parent.getItemAtPosition(position);
                showPurchase(purchaseItem);
            }
        });


    }

    @Override
    public void setBillingService(IInAppBillingService mBillingService) {
        super.setBillingService(mBillingService);
        this.showPurchasableItems();
    }

    abstract public String getPackageName();

    protected void showPurchasableItems() {
        this.showProgressDialog(R.string.dialog_purchases, R.string.dialog_loading_purchases);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> skuList = getProductsIds();

                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                try {
                    Bundle skuDetails = mBillingService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList
                                = skuDetails.getStringArrayList("DETAILS_LIST");

                        final ArrayList<NDPurchaseItem> items = new ArrayList<NDPurchaseItem>();
                        for (String thisResponse : responseList) {
                            NDPurchaseItem purchaseItem = NDPurchaseItem.fromJson(thisResponse, NDPurchaseItem.class);
                            items.add(purchaseItem);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setPurchaseItems(items);
                                hideProgressDialog();
                            }
                        });

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    protected void showPurchase(NDPurchaseItem purchaseItem) {
        this.showProgressDialog(getString(R.string.title_purchasing), purchaseItem.title);

        Bundle buyIntentBundle = null;
        try {
            buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(), purchaseItem.productId, "inapp", null);
            Integer resultCode = buyIntentBundle.getInt("RESPONSE_CODE");
            if (resultCode == 0) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                this.startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } else {
                this.consumePurchase(purchaseItem.productId);
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
            if (requestCode == 1001) {
                this.buyComplete(resultCode, data);
            }
        }
    }

    protected void buyComplete(int resultCode, Intent data) {
        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

        try {
            NDPurchasedItem purchasedItem = NDPurchasedItem.fromJson(purchaseData, NDPurchasedItem.class);
            consumePurchase(purchasedItem.productId);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_error)
                    .setTitle(R.string.message_error_purchases)
                    .show();
            e.printStackTrace();
        }
    }

    protected void consumePurchase(String productId) {
        String purchaseToken = "inapp:" + this.getPackageName() + ":" + productId;
        try {
            mBillingService.consumePurchase(3, this.getPackageName(), purchaseToken);
            hideProgressDialog();

            productBuyComplete(productId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected abstract void productBuyComplete(String sku);

    protected abstract ArrayList<String> getProductsIds();

    public class NDPurchasesAdapter extends BaseAdapter {

        private ArrayList<NDPurchaseItem> mItems;

        @Override
        public int getCount() {
            return this.mItems == null ? 0 : this.mItems.size();
        }

        @Override
        public NDPurchaseItem getItem(int position) {
            return this.mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getViewPurchase(position, convertView, parent);
        }

        public void setPurchaseItems(ArrayList<NDPurchaseItem> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }
    }

    protected abstract View getViewPurchase(int position, View convertView, ViewGroup parent);


}
