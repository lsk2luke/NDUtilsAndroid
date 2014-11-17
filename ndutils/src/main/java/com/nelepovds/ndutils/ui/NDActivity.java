package com.nelepovds.ndutils.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    public void showProgressDialog(String title, String message) {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle(title);
        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void hideProgressDialog() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    public View getContentView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }
}
