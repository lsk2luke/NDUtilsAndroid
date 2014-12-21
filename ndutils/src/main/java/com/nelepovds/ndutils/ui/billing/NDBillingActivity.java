package com.nelepovds.ndutils.ui.billing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.vending.billing.IInAppBillingService;
import com.nelepovds.ndutils.ui.NDActivity;

/**
 * Created by Administrator on 21.12.14.
 */
public class NDBillingActivity extends NDActivity {

    protected ServiceConnection mBillingServiceConn;
    protected IInAppBillingService mBillingService;


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
                mBillingService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        getActivity().bindService(serviceIntent, mBillingServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBillingService != null) {
            this.unbindService(this.mBillingServiceConn);
        }
    }
}
