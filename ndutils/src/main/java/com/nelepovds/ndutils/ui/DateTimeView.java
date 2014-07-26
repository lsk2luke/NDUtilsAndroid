package com.nelepovds.ndutils.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nelepovds.ndutils.CommonUtils;
import com.nelepovds.ndutils.R;

import java.util.Calendar;
import java.util.Date;

public class DateTimeView extends LinearLayout {

    public static final int ND_DTV_DATE = 1;
    public static final int ND_DTV_TIME = 2;
    public static final int ND_DTV_DATE_TIME = 3;

    public static interface IDateTimeViewListener {
        public void didSetNewDateTime(DateTimeView dateTimeView, Calendar calendar);
    }

    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewDateTimeSeparator;
    private String dateTimeSeparator;

    private Date currentDate;
    private int showDateComponents = ND_DTV_DATE_TIME;

    private IDateTimeViewListener dateTimeViewListener;

    public void setDateTimeViewListener(IDateTimeViewListener dateTimeViewListener) {
        this.dateTimeViewListener = dateTimeViewListener;
    }

    public DateTimeView(Context context) {
        super(context);
        init(null, 0);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.setOrientation(LinearLayout.HORIZONTAL);

        //Date
        this.textViewDate = new TextView(getContext());
        this.textViewDateTimeSeparator = new TextView(getContext());
        this.textViewTime = new TextView(getContext());

        this.addView(this.textViewDate);
        this.addView(this.textViewDateTimeSeparator);
        this.addView(this.textViewTime);




        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DateTimeView, defStyle, 0);

        if (a.hasValue(R.styleable.DateTimeView_titleTextColor)) {
            this.textViewDate.setTextColor(a.getColor(R.styleable.DateTimeView_titleTextColor, Color.BLACK));
            this.textViewTime.setTextColor(a.getColor(R.styleable.DateTimeView_titleTextColor, Color.BLACK));
        }

        if (a.hasValue(R.styleable.DateTimeView_dateTimeSeparator)) {
            this.setDateTimeSeparator(a.getString(R.styleable.DateTimeView_dateTimeSeparator));
        } else {
            this.setDateTimeSeparator(" ");
        }

        Date setDate = new Date();
        if (a.hasValue(R.styleable.DateTimeView_dateValue)){
            String dateAttrValue = a.getString(R.styleable.DateTimeView_dateValue);
            setDate = CommonUtils.parseDateTime(dateAttrValue, CommonUtils.DATE_FULL_FORMAT);
        }
        this.setCurrentDate(setDate);
        this.setUpTextViewClickListeners();
        a.recycle();
    }

    private void setUpTextViewClickListeners() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.currentDate);
        this.textViewDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        DateTimeView.this.setCurrentDate(calendar.getTime());

                        if (dateTimeViewListener != null) {
                            dateTimeViewListener.didSetNewDateTime(DateTimeView.this, calendar);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this.textViewTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);//Always reset to zero
                        DateTimeView.this.setCurrentDate(calendar.getTime());
                        if (dateTimeViewListener != null) {
                            dateTimeViewListener.didSetNewDateTime(DateTimeView.this, calendar);
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });
    }

    public TextView getTextViewDate() {
        return textViewDate;
    }

    public TextView getTextViewTime() {
        return textViewTime;
    }

    public void setDateTimeSeparator(String dateTimeSeparator) {
        this.dateTimeSeparator = dateTimeSeparator;
        this.textViewDateTimeSeparator.setText(this.dateTimeSeparator);
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
        textViewDate.setText(CommonUtils.formatDate(currentDate, CommonUtils.DATE_FORMAT));
        textViewTime.setText(CommonUtils.formatDate(currentDate, CommonUtils.DATE_TIME_FORMAT));
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setShowDateComponents(int showDateComponents) {
        this.showDateComponents = showDateComponents;
        switch (showDateComponents) {
            case ND_DTV_DATE:
                this.textViewDate.setVisibility(View.VISIBLE);
                this.textViewTime.setVisibility(View.GONE);
                break;
            case ND_DTV_TIME:
                this.textViewDate.setVisibility(View.GONE);
                this.textViewTime.setVisibility(View.VISIBLE);
                break;
            case ND_DTV_DATE_TIME:
                this.textViewDate.setVisibility(View.VISIBLE);
                this.textViewTime.setVisibility(View.VISIBLE);
                break;
        }
    }
}
