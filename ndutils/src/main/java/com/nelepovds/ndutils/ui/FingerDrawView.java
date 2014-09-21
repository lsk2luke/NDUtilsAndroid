package com.nelepovds.ndutils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.nelepovds.ndutils.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by dmitrynelepov on 08.07.14.
 */
public class FingerDrawView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private int currentColor = 0xff000000;
    private int currentAlpha = 255;
    private int currentBackgroundColor = Color.WHITE;
    private int currentStrokeWidth = 1;
    private File cacheDir;
    private GestureDetector gestureDetector;

    public Bitmap getCurrentBitmap() {
        return this.canvasBitmap;
    }


    public static interface IFingerDrawViewListener {

        void changeHistory(ArrayList<File> history);
    }

    public IFingerDrawViewListener fingerDrawViewListener;


    public static final int FDV_MAX_HISTORY_SIZE = 10;

    private int historySize = FDV_MAX_HISTORY_SIZE;

    public ArrayList<File> historyDraw = new ArrayList<File>(FDV_MAX_HISTORY_SIZE);

    public FingerDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }


    public void setCacheDir(File cacheDir) {
        this.cacheDir = new File(cacheDir, "fingerDrawTemp");
        this.cacheDir.mkdirs();
        this.cleanUpCache();

    }

    private void cleanUpCache() {
        //Clean up
        for (File fileHistory : this.cacheDir.listFiles()) {
            fileHistory.delete();
            fileHistory.deleteOnExit();
        }
    }

    //setup drawing
    private void setupDrawing() {

        //prepare for drawing and setup paint stroke properties
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(currentColor);
        drawPaint.setDither(true);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(this.currentStrokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setAlpha(this.currentAlpha);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        this.gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                String swipe = "";
                float sensitvity = 50;

                if ((e1.getX() - e2.getX()) > sensitvity) {
                    makedSwipeLeft();
                } else if ((e2.getX() - e1.getX()) > sensitvity) {
                    makedSwipeRight();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    private void makedSwipeRight() {
        if (this.historyDraw.size() > 1) {
            Log.wtf("GO BACK", "Reverse");
        }
    }

    private void makedSwipeLeft() {

    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            try {
                if (canvasBitmap == null) {
                    canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    canvasBitmap.eraseColor(this.currentBackgroundColor);
                }
                drawCanvas = new Canvas(canvasBitmap);
            } catch (Exception ex) {

            }
        }
    }

    public void setCurrentDrawState(Bitmap bmp) {
        this.canvasBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        this.drawCanvas = new Canvas(this.canvasBitmap);
        invalidate();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.cleanUpCache();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minDimension = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(minDimension, minDimension);
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        } catch (NullPointerException npe) {

        }

    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = -1;
        float touchY = -1;
        touchX = event.getX();
        touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                if (drawPath.isEmpty() == Boolean.FALSE) {
                    drawPath.lineTo(touchX, touchY);
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    this.addDataToHistory();
                }

                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;

    }


    private void addDataToHistory() {
        if (this.cacheDir != null) {
            String dateTimeFile = CommonUtils.formatDate(new Date(), CommonUtils.DATE_FULL_FORMAT_FILE_SAVE);
            String fileNameSave = "TempFingerDraw_" + dateTimeFile + ".png";
            File cacheTempImage = new File(this.cacheDir, fileNameSave);
            try {
                if (canvasBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(cacheTempImage))) {
                    this.historyDraw.add(0, cacheTempImage);
                    if (this.historyDraw.size() > this.historySize) {
                        this.historyDraw.remove(this.historyDraw.size() - 1);
                    }
                    if (this.fingerDrawViewListener != null) {
                        this.fingerDrawViewListener.changeHistory(this.historyDraw);
                    }
                }
            } catch (Exception ex) {

            }
        }
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
        this.drawPaint.setColor(this.currentColor);
        this.drawPaint.setAlpha(this.currentAlpha);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public int getCurrentAlpha() {
        return currentAlpha;
    }

    public void setCurrentAlpha(int currentAlpha) {
        this.currentAlpha = currentAlpha;
        this.drawPaint.setAlpha(currentAlpha);
    }

    public int getCurrentStrokeWidth() {
        return currentStrokeWidth;
    }

    public void setCurrentStrokeWidth(int currentStrokeWidth) {
        this.currentStrokeWidth = currentStrokeWidth;
        this.drawPaint.setStrokeWidth(currentStrokeWidth);
    }

    public int getCurrentBackgroundColor() {
        return currentBackgroundColor;
    }

    public void setCurrentBackgroundColor(int currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }
}
