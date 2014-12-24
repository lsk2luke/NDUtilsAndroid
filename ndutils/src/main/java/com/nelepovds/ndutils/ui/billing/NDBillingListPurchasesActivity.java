package com.nelepovds.ndutils.ui.billing;

import android.os.Bundle;
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
        this.adapter = new NDPurchasesAdapter();
        this.listViewPurchases.setAdapter(this.adapter);
        this.listViewPurchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NDPurchaseItem purchaseItem = (NDPurchaseItem) parent.getItemAtPosition(position);
                buyItem(purchaseItem);
            }
        });
        this.initBilling();
    }

    @Override
    public void setBillingService(IInAppBillingService mBillingService) {
        super.setBillingService(mBillingService);
        this.showPurchasableItems();
    }

    protected void showPurchasableItems() {
        this.showProgressDialog(R.string.dialog_purchases, R.string.dialog_loading_purchases);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> skuList = getProductsIds();
                final ArrayList<NDPurchaseItem> items = loadPurchasableItems(skuList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setPurchaseItems(items);
                        hideProgressDialog();
                    }
                });

            }
        }).start();

    }


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
