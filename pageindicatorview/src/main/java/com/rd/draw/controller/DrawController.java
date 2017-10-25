package com.rd.draw.controller;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rd.animation.data.Value;
import com.rd.animation.type.AnimationType;
import com.rd.draw.data.Indicator;
import com.rd.draw.drawer.Drawer;
import com.rd.utils.CoordinatesUtils;


public class DrawController {

    private Value value;
    private Drawer drawer;
    private Indicator indicator;
    private Paint pageNumberPaint;

    public DrawController(@NonNull Indicator indicator) {
        this.indicator = indicator;
        this.drawer = new Drawer(indicator);
    }

    public void updateValue(@Nullable Value value) {
        this.value = value;
    }

    public void draw(@NonNull Canvas canvas) {
        int count = indicator.getCount();
        pageNumberPaint = new Paint();
        pageNumberPaint.setStyle(Paint.Style.FILL);
        pageNumberPaint.setAntiAlias(true);
        pageNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);

        boolean isShowPageNumber = indicator.isShowPageNumber();
        int pageNumberUnselectedColor = indicator.getPageNumberUnSelectColor();
        int pageNumberSelectedColor = indicator.getPageNumberSelectColor();

        int radius = indicator.getRadius();
        pageNumberPaint.setTextSize(new Double(radius * 1.2).floatValue());

        for (int position = 0; position < count; position++) {
            int coordinateX = CoordinatesUtils.getXCoordinate(indicator, position);
            int coordinateY = CoordinatesUtils.getYCoordinate(indicator, position);

            drawIndicator(canvas, position, coordinateX, coordinateY);

            drawPageNumber(isShowPageNumber,position,pageNumberUnselectedColor,canvas);
        }

        drawPageNumber(isShowPageNumber,indicator.getSelectedPosition(),pageNumberSelectedColor,canvas);
    }

    private void drawIndicator(
            @NonNull Canvas canvas,
            int position,
            int coordinateX,
            int coordinateY) {

        boolean interactiveAnimation = indicator.isInteractiveAnimation();
        int selectedPosition = indicator.getSelectedPosition();
        int selectingPosition = indicator.getSelectingPosition();
        int lastSelectedPosition = indicator.getLastSelectedPosition();

        boolean selectedItem = !interactiveAnimation && (position == selectedPosition || position == lastSelectedPosition);
        boolean selectingItem = interactiveAnimation && (position == selectedPosition || position == selectingPosition);
        boolean isSelectedItem = selectedItem | selectingItem;
        drawer.setup(position, coordinateX, coordinateY);

        if (value != null && isSelectedItem) {
            drawWithAnimation(canvas);
        } else {
            drawer.drawBasic(canvas, isSelectedItem);
        }
    }

    private void drawWithAnimation(@NonNull Canvas canvas) {
        AnimationType animationType = indicator.getAnimationType();
        switch (animationType) {
            case NONE:
                drawer.drawBasic(canvas, true);
                break;

            case COLOR:
                drawer.drawColor(canvas, value);
                break;

            case SCALE:
                drawer.drawScale(canvas, value);
                break;

            case WORM:
                drawer.drawWorm(canvas, value);
                break;

            case SLIDE:
                drawer.drawSlide(canvas, value);
                break;

            case FILL:
                drawer.drawFill(canvas, value);
                break;

            case THIN_WORM:
                drawer.drawThinWorm(canvas, value);
                break;

            case DROP:
                drawer.drawDrop(canvas, value);
                break;

            case SWAP:
                drawer.drawSwap(canvas, value);
                break;
        }
    }

    private void drawPageNumber(boolean isShow,int position,int color,Canvas canvas){
        if (!isShow) return;

        pageNumberPaint.setColor(color);

        float textWidth = pageNumberPaint.measureText((position+1)+"");
        Paint.FontMetrics fm = pageNumberPaint.getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
        int coordinateX = CoordinatesUtils.getXCoordinate(indicator, position);
        int coordinateY = CoordinatesUtils.getYCoordinate(indicator, position);
        canvas.drawText((position+1)+"",coordinateX-textWidth/2,coordinateY+textHeight/4,pageNumberPaint);

    }
}
