package com.nelepovds.ndutils.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nelepovds.ndutils.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dmitrynelepov on 12.06.14.
 */
public class NDDateTimePickerDialog extends Dialog {

    private final NDDateTimePickerDialogTypes typeDialog;

    public static enum NDDateTimePickerDialogTypes {
        NDDateTimePickerDialogTypeDate,
        NDDateTimePickerDialogTypeTime,
        NDDateTimePickerDialogTypeDateTime,
    }

    public ListView _yearList;
    public IntegerAdapter _yearAdapter;
    public ListView _monthList;
    public ListView _dayList;
    public ListView _hourList;
    public ListView _minuteList;

    //Init params
    private Calendar minDate;
    private Calendar maxDate;

    private Calendar currentDateTime;

    public NDDateTimePickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener, NDDateTimePickerDialogTypes typeDialog) {
        super(context, cancelable, cancelListener);
        this.setContentView(R.layout.ndutils_datetime_picker_dialog_layout);
        this.typeDialog = typeDialog;

        this.initControls();
        this.setCurrentDateTime(new Date());
    }

    private void initControls() {
        this.minDate = Calendar.getInstance();
        this.minDate.setTime(new Date(Long.MIN_VALUE));

        this.maxDate = Calendar.getInstance();
        this.maxDate.setTime(new Date(Long.MAX_VALUE));

        this._yearList = (ListView) findViewById(R.id._yearList);
        this._yearAdapter = new IntegerAdapter(this.getContext());
        this._yearList.setAdapter(this._yearAdapter);
        this._yearAdapter.start = this.minDate.getMinimum(Calendar.YEAR);
        this._yearAdapter.end = this.maxDate.getMaximum(Calendar.YEAR);
        this._yearAdapter.notifyDataSetChanged();

        this._monthList = (ListView) findViewById(R.id._monthList);
        this._dayList = (ListView) findViewById(R.id._dayList);
        this._hourList = (ListView) findViewById(R.id._hourList);
        this._minuteList = (ListView) findViewById(R.id._minuteList);

    }

    public void setCurrentDateTime(Date pCurrentDateTime) {
        this.currentDateTime = Calendar.getInstance();
        this.currentDateTime.setTime(pCurrentDateTime);
        //Year
        this.fillUpYearWithValue(this.currentDateTime.get(Calendar.YEAR));
    }

    private void fillUpYearWithValue(int year) {

    }

    //Classes
    private class IntegerAdapter extends BaseAdapter {
        private Context context;
        public Integer start = 0;
        public Integer end = 0;

        public IntegerAdapter( Context pContext) {
            this.context = pContext;
        }


        @Override
        public int getCount() {
            return end - start;
        }

        @Override
        public Integer getItem(int i) {
            return Integer.valueOf(start + i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView retView = new TextView(this.context);
            retView.setText(this.getItem(i).toString());
            retView.setTextColor(Color.WHITE);
            return retView;
        }
    }
}
