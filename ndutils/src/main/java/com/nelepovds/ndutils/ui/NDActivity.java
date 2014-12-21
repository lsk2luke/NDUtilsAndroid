package com.nelepovds.ndutils.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

/**
 * Created by dmitrynelepov on 26.09.14.
 */
public class NDActivity extends Activity {


    protected ProgressDialog progressDialog;
    protected AlertDialog alertDialog;

    protected void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        this.initControls();
    }

    public void initControls() {
        NDUIWidgetHelper.initWidgets(this);

    }

    public NDActivity getActivity() {
        return this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.hideProgressDialog();
    }

    public void showProgressDialog(int title, int message) {
        this.showProgressDialog(getString(title), getString(message));
    }

    public void showProgressDialog(String title, String message) {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle(title);
        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    public AlertDialog showAlertDialog(int title, int message) {
        if (this.alertDialog != null) {
            this.alertDialog.dismiss();
        }
        this.alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).create();
        this.alertDialog.show();
        return this.alertDialog;
    }

    public View getContentView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }


}
