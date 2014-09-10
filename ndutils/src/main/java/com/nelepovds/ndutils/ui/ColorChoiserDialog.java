package com.nelepovds.ndutils.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nelepovds.ndutils.R;


/**
 * Created by dmitrynelepov on 08.07.14.
 */
public class ColorChoiserDialog extends AlertDialog implements View.OnTouchListener, View.OnClickListener {


    public static interface IColorChoiserDialogListener {

        public void didSelectColorHSV(float colorH, float colorS, float colorV);
    }

    private IColorChoiserDialogListener colorChoiserDialogListener;

    public static final float HSV_MAX = 360.0f;

    public ImageView imageViewColorPresent;
    public RelativeLayout relativeLayoutVS;
    public float colorH = 0;
    public float colorS = 1;
    public float colorV = 1;

    private float touchValue = 0;
    private float touchY;

    public Button buttonSelectColor;

    public LinearLayout linearLayoutFirstColorPart;
    public TextView textViewFirstColorPart;
    public LinearLayout linearLayoutSecondColorPart;
    public TextView textViewSecondColorPart;
    public LinearLayout linearLayoutThirdColorPart;
    public TextView textViewThirdColorPart;

    public ColorChoiserDialog(Context context, IColorChoiserDialogListener colorChoiserDialogListener) {
        super(context);
        this.colorChoiserDialogListener = colorChoiserDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_choiser);

        this.linearLayoutFirstColorPart = (LinearLayout) findViewById(R.id.linearLayoutFirstColorPart);
        this.linearLayoutFirstColorPart.setOnTouchListener(this);
        this.textViewFirstColorPart = (TextView) findViewById(R.id.textViewFirstColorPart);

        this.linearLayoutSecondColorPart = (LinearLayout) findViewById(R.id.linearLayoutSecondColorPart);
        this.linearLayoutSecondColorPart.setOnTouchListener(this);
        this.textViewSecondColorPart = (TextView) findViewById(R.id.textViewSecondColorPart);

        this.linearLayoutThirdColorPart = (LinearLayout) findViewById(R.id.linearLayoutThirdColorPart);
        this.linearLayoutThirdColorPart.setOnTouchListener(this);
        this.textViewThirdColorPart = (TextView) findViewById(R.id.textViewThirdColorPart);


        this.relativeLayoutVS = (RelativeLayout) findViewById(R.id.relativeLayoutVS);
        this.relativeLayoutVS.setOnTouchListener(this);

        this.imageViewColorPresent = (ImageView) findViewById(R.id.imageViewColorPresent);
        this.updateHSV();

        this.buttonSelectColor = (Button) findViewById(R.id.buttonSelectColor);
        this.buttonSelectColor.setOnClickListener(this);

    }

    public ColorChoiserDialog setHSV(float h, float s, float v) {
        this.colorH = h;
        this.colorS = s;
        this.colorV = v;
        return this;
    }

    public void updateHSV() {
        int newColor = Color.HSVToColor(new float[]{colorH, colorS, colorV});
        this.imageViewColorPresent.setImageDrawable(new ColorDrawable(newColor));
        this.textViewFirstColorPart.setText(String.format("%.0f",this.colorH));
        this.textViewSecondColorPart.setText(String.format("%.2f", this.colorS));
        this.textViewThirdColorPart.setText(String.format("%.2f", this.colorV));

        int size = imageViewColorPresent.getWidth();
        int borderWidth =  (int) getContext().getResources().getDimension(R.dimen.size8);

        //Update line params
        GradientDrawable circle = new GradientDrawable();
        circle.setColor(newColor);
        circle.setShape(GradientDrawable.OVAL);
        circle.setSize(size - borderWidth, size - borderWidth);
        this.imageViewColorPresent.setImageDrawable(circle);

        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT);
        border.setShape(GradientDrawable.OVAL);
        border.setStroke((int) getContext().getResources().getDimension(R.dimen.size4), Color.WHITE);
        border.setSize(size + borderWidth, size + borderWidth);
        this.relativeLayoutVS.setBackgroundDrawable(border);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchY = event.getY();
            if (v.getId() == this.linearLayoutFirstColorPart.getId()) {
                this.touchValue = this.colorH;
            } else if (v.getId() == this.linearLayoutSecondColorPart.getId()) {
                this.touchValue = this.colorS * HSV_MAX;
            } else if (v.getId() == this.linearLayoutThirdColorPart.getId()) {
                this.touchValue = this.colorV * HSV_MAX;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float touchDiff = this.touchValue - (event.getY() - this.touchY);
            if (v.getId() == this.linearLayoutFirstColorPart.getId()) {
                this.colorH = (int) Math.min(HSV_MAX, Math.max(0, touchDiff));
            } else if (v.getId() == this.linearLayoutSecondColorPart.getId()) {
                this.colorS = Math.min(HSV_MAX, Math.max(0, touchDiff)) / HSV_MAX;
            } else if (v.getId() == this.linearLayoutThirdColorPart.getId()) {
                this.colorV = Math.min(HSV_MAX, Math.max(0, touchDiff)) / HSV_MAX;
            }

            this.updateHSV();
        }

        return true;
    }

    public void setColorChoiserDialogListener(IColorChoiserDialogListener colorChoiserDialogListener) {
        this.colorChoiserDialogListener = colorChoiserDialogListener;
    }

    @Override
    public void onClick(View v) {
        if (this.colorChoiserDialogListener != null) {
            this.colorChoiserDialogListener.didSelectColorHSV(this.colorH, this.colorS, this.colorV);
            this.dismiss();
        }
    }


}
