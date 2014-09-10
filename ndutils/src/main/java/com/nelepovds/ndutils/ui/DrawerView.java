package com.nelepovds.ndutils.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nelepovds.ndutils.R;

import java.io.File;



/**
 * Created by dmitrynelepov on 11.09.14.
 */
public class DrawerView extends LinearLayout implements View.OnClickListener, LineSizeDialog.ILineSizeDialogListener, ColorChoiserDialog.IColorChoiserDialogListener {

    public FingerDrawView fingerDrawView;

    public GridView gridViewColors;
    public ColorsGridAdapter colorsGridAdapter;

    public ImageView imageViewEraser;
    public ImageView imageViewLineSizes;
    public LinearLayout linearLayoutLineParams;
    public RelativeLayout relativeLayoutLineParameters;

    public DrawerView(Context context) {
        super(context);
        this.setupUI();
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setupUI();
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setupUI();
    }

    public DrawerView setCacheDir(File cacheDir) {
        this.fingerDrawView.setCacheDir(cacheDir);
        return this;
    }

    private void setupUI() {
        inflate(getContext(), R.layout.nd_drawer_view,this);
        this.fingerDrawView = (FingerDrawView) findViewWithTag("ndDrawerFingerDrawView");
        //TODO: 4 - заменить на парамтер
        this.fingerDrawView.setCurrentStrokeWidth(4);


        this.gridViewColors = (GridView) findViewWithTag("ndDrawerGridViewColors");

        this.imageViewEraser = (ImageView) findViewWithTag("ndDrawerImageViewEraser");
        this.imageViewLineSizes = (ImageView) findViewWithTag("ndDrawerImageViewLineSizes");
        this.linearLayoutLineParams = (LinearLayout) findViewWithTag("ndDrawerLinearLayoutLineParams");
        this.relativeLayoutLineParameters = (RelativeLayout) findViewWithTag("ndDrawerRelativeLayoutLineParameters");


        this.imageViewEraser.setOnClickListener(this);
        this.linearLayoutLineParams.setOnClickListener(this);

        this.createColors();
        this.updateLineParams();
    }

    @Override
    public void onClick(View v) {
        String id = v.getTag().toString();

        if (id.equalsIgnoreCase(this.linearLayoutLineParams.getTag().toString())) {
            new LineSizeDialog(this.getContext(), this).initDialog(this.fingerDrawView.getCurrentAlpha(), this.fingerDrawView.getCurrentStrokeWidth(), this.fingerDrawView.getCurrentColor()).show();
        }
        if (id.equalsIgnoreCase(this.imageViewEraser.getTag().toString())) {
            this.fingerDrawView.setCurrentColor(this.fingerDrawView.getCurrentBackgroundColor());
            this.fingerDrawView.setAlpha(255);
            this.updateLineParams();
        }


    }

    @Override
    public void didSelectColorHSV(float colorH, float colorS, float colorV) {
        this.fingerDrawView.setCurrentColor(Color.HSVToColor(new float[]{colorH, colorS, colorV}));
    }

    @Override
    public void applyLineParams(int alpha, int lineSize) {
        this.fingerDrawView.setCurrentAlpha(alpha);
        this.fingerDrawView.setCurrentStrokeWidth(lineSize);
        this.updateLineParams();
    }

    public void updateLineParams() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.imageViewLineSizes.getLayoutParams();
        lp.width = this.fingerDrawView.getCurrentStrokeWidth();
        lp.height = this.fingerDrawView.getCurrentStrokeWidth();
        this.imageViewLineSizes.setLayoutParams(lp);

        this.imageViewLineSizes.setImageResource(0);
        //Update line params
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(new OvalShape());
        shapeDrawable.setIntrinsicHeight(this.fingerDrawView.getCurrentStrokeWidth());
        shapeDrawable.setIntrinsicWidth(this.fingerDrawView.getCurrentStrokeWidth());
        shapeDrawable.getPaint().setColor(this.fingerDrawView.getCurrentColor());
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAlpha(this.fingerDrawView.getCurrentAlpha());
        shapeDrawable.getPaint().setAntiAlias(Boolean.TRUE);
        this.imageViewLineSizes.setImageDrawable(shapeDrawable);

        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT);
        border.setShape(GradientDrawable.OVAL);
        //TODO: 2 - заменить на парамтер
        border.setStroke((int) 2, Color.WHITE);
        border.setSize(this.relativeLayoutLineParameters.getWidth(), this.relativeLayoutLineParameters.getWidth());
        this.relativeLayoutLineParameters.setBackgroundDrawable(border);
    }

    /**
     * Create colors preset
     */
    private void createColors() {
        this.gridViewColors.requestLayout();
        this.gridViewColors.setNumColumns(11);
        this.colorsGridAdapter = new ColorsGridAdapter();
        this.gridViewColors.setAdapter(this.colorsGridAdapter);
        this.gridViewColors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fingerDrawView.setCurrentColor(colorsGridAdapter.getItem(position));
                updateLineParams();
                colorsGridAdapter.setSelectedItem(position);
            }
        });

        this.gridViewColors.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showColorChoiserDialog(colorsGridAdapter.getItem(position));
                return false;
            }
        });

        this.gridViewColors.setSelection(0);

    }

    private void showColorChoiserDialog(Integer item) {
        float[] hsv = new float[3];
        Color.colorToHSV(item, hsv);
        new ColorChoiserDialog(this.getContext(), this).setHSV(hsv[0], hsv[1], hsv[2]).show();
    }
}
