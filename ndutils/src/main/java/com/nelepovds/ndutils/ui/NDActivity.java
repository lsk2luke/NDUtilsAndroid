package com.nelepovds.ndutils.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

/**
 * Created by dmitrynelepov on 26.09.14.
 */
public class NDActivity extends Activity {


    protected ProgressDialog progressDialog;

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

    public View getContentView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            this.clickHomeButton();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    protected void clickHomeButton() {
//        finish();
//    }

}
