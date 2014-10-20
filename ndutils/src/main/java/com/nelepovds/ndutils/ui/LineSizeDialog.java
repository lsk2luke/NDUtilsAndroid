package com.nelepovds.ndutils.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nelepovds.ndutils.R;


/**
 * Created by dmitrynelepov on 09.07.14.
 */
public class LineSizeDialog extends AlertDialog implements View.OnClickListener, View.OnTouchListener {


    public static interface ILineSizeDialogListener {

        public void applyLineParams(int alpha, int lineSize);
    }

    private ILineSizeDialogListener lineSizeDialogListener;

    private int alpha;
    private int lineSize;
    private int touchValue;
    private int maxSize;

    private int currentColor;
    private float touchY;

    public RelativeLayout relativeLayoutLineSize;
    public RelativeLayout relativeLayoutLineAlpha;


    private RelativeLayout relativeLayoutLineSizeHolder;
    private ImageView imageViewLinePreview;
    private Button buttonApplyParams;

    private TextView textViewDialogLineWidth;
    private TextView textViewDialogLineAlpha;

    public LineSizeDialog(Context context, ILineSizeDialogListener lineSizeDialogListener) {
        super(context);
        this.lineSizeDialogListener = lineSizeDialogListener;
        this.maxSize = (int) context.getResources().getDimension(R.dimen.size32);

    }

    public LineSizeDialog initDialog(int alpha, int lineSize, int currentColor) {
        this.alpha = alpha;
        this.lineSize = lineSize;
        this.currentColor = currentColor;
        this.updateLineParams();
        return this;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_line_choiser);

        this.textViewDialogLineAlpha = (TextView)findViewById(R.id.textViewDialogLineAlpha);
        this.textViewDialogLineWidth = (TextView)findViewById(R.id.textViewDialogLineWidth);

        this.imageViewLinePreview = (ImageView) findViewById(R.id.imageViewLinePreview);
        this.relativeLayoutLineSizeHolder = (RelativeLayout) findViewById(R.id.relativeLayoutLineSizeHolder);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.relativeLayoutLineSizeHolder.getLayoutParams();
        layoutParams.height = layoutParams.width = (int) (this.maxSize * 2 + getContext().getResources().getDimension(R.dimen.size8));
        this.relativeLayoutLineSizeHolder.setLayoutParams(layoutParams);

        this.relativeLayoutLineSize = (RelativeLayout)findViewById(R.id.relativeLayoutLineSize);
        this.relativeLayoutLineAlpha = (RelativeLayout)findViewById(R.id.relativeLayoutLineAlpha);

        this.relativeLayoutLineAlpha.setOnTouchListener(this);
        this.relativeLayoutLineSize.setOnTouchListener(this);

        this.buttonApplyParams = (Button) findViewById(R.id.buttonApplyParams);
        this.buttonApplyParams.setOnClickListener(this);
        this.buttonApplyParams.setEnabled(true);
        this.updateLineParams();
    }

    private void updateLineParams() {
        if (this.imageViewLinePreview != null) {
            int newColor = Color.argb(this.alpha, Color.red(this.currentColor), Color.green(this.currentColor), Color.blue(this.currentColor));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.imageViewLinePreview.getLayoutParams();
            layoutParams.width = layoutParams.height = this.lineSize * 2;
            this.imageViewLinePreview.setLayoutParams(layoutParams);
            this.imageViewLinePreview.setImageResource(0);
            //Update line params
            GradientDrawable circle = new GradientDrawable();
            circle.setColor(newColor);
            circle.setShape(GradientDrawable.OVAL);
            circle.setSize(layoutParams.width, layoutParams.width);
            this.imageViewLinePreview.setImageDrawable(circle);

            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.TRANSPARENT);
            border.setShape(GradientDrawable.OVAL);
            border.setStroke((int) getContext().getResources().getDimension(R.dimen.size8), Color.WHITE);
            border.setSize(this.maxSize, this.maxSize);
            this.relativeLayoutLineSizeHolder.setBackgroundDrawable(border);

            this.textViewDialogLineAlpha.setText(String.valueOf(this.alpha));
            this.textViewDialogLineWidth.setText(String.valueOf(this.lineSize));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.buttonApplyParams.getId()) {
            if (this.lineSizeDialogListener != null) {
                this.lineSizeDialogListener.applyLineParams(this.alpha, this.lineSize);
                this.dismiss();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchY = event.getY();
            if (v.getId() == this.relativeLayoutLineSize.getId()) {
                this.touchValue = this.lineSize;
            } else if (v.getId() == this.relativeLayoutLineAlpha.getId()) {
                this.touchValue = this.alpha;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (v.getId() == this.relativeLayoutLineSize.getId()) {
                this.lineSize = (int) Math.min(this.maxSize, Math.max(1, this.touchValue - (event.getY() - this.touchY)));
            } else if (v.getId() == this.relativeLayoutLineAlpha.getId()) {
                this.alpha = (int) Math.min(255, Math.max(0, this.touchValue - (this.touchY - event.getY())));
            }

            this.updateLineParams();
        }
        return true;
    }

    public ILineSizeDialogListener getLineSizeDialogListener() {
        return lineSizeDialogListener;
    }

    public void setLineSizeDialogListener(ILineSizeDialogListener lineSizeDialogListener) {
        this.lineSizeDialogListener = lineSizeDialogListener;
    }
}