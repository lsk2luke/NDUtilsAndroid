package com.nelepovds.ndutils.ui;

import android.app.Activity;
import android.os.Bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dmitrynelepov on 26.09.14.
 */
public class NDActivity extends Activity {


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
}
