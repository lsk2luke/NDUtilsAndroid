package com.nelepovds.ndutils.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nelepovds.ndutils.R;


/**
 * Created by dmitrynelepov on 02.09.14.
 */
public class ColorsGridAdapter extends BaseAdapter {
    private int selectedItem = -1;

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public Integer getItem(int position) {
        int getColor = Color.HSVToColor(new float[]{position * 36, position * 1.00f, position * 1.00f});
        return Integer.valueOf(getColor);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ColorGridItemView retView = (ColorGridItemView) convertView;
        retView = new ColorGridItemView(parent.getContext());
        retView.setColorItem(getItem(position));
        retView.setSelectedItem(position == this.selectedItem);
        return retView;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        this.notifyDataSetChanged();
    }


    class ColorGridItemView extends RelativeLayout {

        public Integer colorItem;

        private ImageView imageViewColorItem;
        private boolean selectedItem;


        public ColorGridItemView(Context context) {
            super(context);
            this.init();
        }

        private void init() {
            inflate(getContext(), R.layout.gridview_color_item, this);
            this.imageViewColorItem = (ImageView) findViewById(R.id.imageViewColorItem);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            GradientDrawable circle = new GradientDrawable();
            circle.setColor(this.colorItem);
            circle.setShape(GradientDrawable.OVAL);
            circle.setSize(getMeasuredWidthAndState(), getMeasuredWidthAndState());
            circle.setStroke((int) getResources().getDimension(R.dimen.size8), Color.TRANSPARENT);

            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(new OvalShape());
            shapeDrawable.setIntrinsicHeight(getMeasuredWidthAndState());
            shapeDrawable.setIntrinsicWidth(getMeasuredWidthAndState());
            shapeDrawable.getPaint().setColor(colorItem);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
            this.imageViewColorItem.setBackgroundDrawable(circle);


            if (this.selectedItem) {
                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.TRANSPARENT);
                border.setShape(GradientDrawable.OVAL);
                border.setStroke((int) getResources().getDimension(R.dimen.size2), Color.WHITE);
                border.setSize(getMeasuredWidthAndState(), getMeasuredWidthAndState());
                this.imageViewColorItem.setImageDrawable(border);
            } else {
                this.imageViewColorItem.setImageDrawable(null);
            }
        }

        public void setColorItem(Integer colorItem) {
            this.colorItem = colorItem;
        }

        public void setSelectedItem(boolean selectedItem) {
            this.selectedItem = selectedItem;
        }
    }
}
