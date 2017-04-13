package com.rd.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import com.rd.animation.data.Value;
import com.rd.animation.data.type.ColorAnimationValue;
import com.rd.animation.type.AnimationType;
import com.rd.draw.data.Indicator;
import com.rd.utils.CoordinatesUtils;

public class DrawManager {

    private Paint fillPaint;
    private Paint strokePaint;
    private RectF rect;

    private Indicator indicator;
    private Canvas canvas;

    public DrawManager(@NonNull Canvas canvas) {
        this.canvas = canvas;
        this.indicator = new Indicator();

        this.fillPaint = new Paint();
        this.strokePaint = new Paint();
        this.rect = new RectF();
    }

    @NonNull
    public Indicator indicator() {
        if (indicator == null) {
            indicator = new Indicator();
        }

        return indicator;
    }

    public void draw(@NonNull Value value) {
        int count = indicator.getCount();

        for (int position = 0; position < count; position++) {
            int coordinateX = CoordinatesUtils.getXCoordinate(indicator, position);
            int coordinateY = CoordinatesUtils.getYCoordinate(indicator, position);
            drawIndicator(canvas, value, position, coordinateX, coordinateY);
        }
    }

    private void drawIndicator(
            @NonNull Canvas canvas,
            @NonNull Value value,
            int position,
            int coordinateX,
            int coordinateY) {

        boolean interactiveAnimation = indicator.isInteractiveAnimation();
        int selectedPosition = indicator.getSelectedPosition();
        int selectingPosition = indicator.getSelectingPosition();
        int lastSelectedPosition = indicator.getLastSelectedPosition();

        boolean selectedItem = !interactiveAnimation && (position == selectedPosition || position == lastSelectedPosition);
        boolean selectingItem = interactiveAnimation && (position == selectingPosition || position == selectedPosition);
        boolean isSelectedItem = selectedItem | selectingItem;

        if (isSelectedItem) {
            drawWithAnimationEffect(canvas, value, position, coordinateX, coordinateY);
        } else {
            drawWithNoEffect(canvas, position, coordinateX, coordinateY);
        }
    }

    private void drawWithAnimationEffect(
            @NonNull Canvas canvas,
            @NonNull Value value,
            int position,
            int coordinateX,
            int coordinateY) {

        AnimationType animationType = indicator.getAnimationType();
        switch (animationType) {
            case NONE:
                drawWithNoEffect(canvas, position, coordinateX, coordinateY);
                break;

            case COLOR:
                if (value instanceof ColorAnimationValue) {
                    drawWithColorAnimation(canvas, (ColorAnimationValue) value, position, coordinateX, coordinateY);
                }
                break;

//            case SCALE:
//                drawWithScaleAnimation(canvas, position, coordinateX, coordinateY);
//                break;
//
//            case SLIDE:
//                drawWithSlideAnimation(canvas, position, coordinateX, coordinateY);
//                break;
//
//            case WORM:
//                drawWithWormAnimation(canvas, coordinateX, coordinateY);
//                break;
//
//            case FILL:
//                drawWithFillAnimation(canvas, position, coordinateX, coordinateY);
//                break;
//
//            case THIN_WORM:
//                drawWithThinWormAnimation(canvas, coordinateX, coordinateY);
//                break;
//
//            case DROP:
//                drawWithDropAnimation(canvas, coordinateX, coordinateY);
//                break;
//
//            case SWAP:
//                if (orientation == Orientation.HORIZONTAL)
//                    drawWithSwapAnimation(canvas, position, coordinateX, coordinateY);
//                else
//                    drawWithSwapAnimationVertically(canvas, position, coordinateX, coordinateY);
//                break;
        }
    }

    private void drawWithNoEffect(
            @NonNull Canvas canvas,
            int position,
            int coordinateX,
            int coordinateY) {

        float radius = indicator.getRadiusPx();
        int strokePx = indicator.getStrokePx();
        int scaleFactor = indicator.getScaleFactor();
        int selectedColor = indicator.getSelectedColor();
        int unselectedColor = indicator.getUnselectedColor();
        int selectedPosition = indicator.getSelectedPosition();
        AnimationType animationType = indicator.getAnimationType();

        if (animationType == AnimationType.SCALE) {
            radius *= scaleFactor;
        }

        int color = unselectedColor;
        if (position == selectedPosition) {
            color = selectedColor;
        }

        Paint paint;
        if (animationType == AnimationType.FILL) {
            paint = strokePaint;
            paint.setStrokeWidth(strokePx);
        } else {
            paint = fillPaint;
        }

        paint.setColor(color);
        canvas.drawCircle(coordinateX, coordinateY, radius, paint);
    }

    private void drawWithColorAnimation(
            @NonNull Canvas canvas,
            @NonNull ColorAnimationValue value,
            int position,
            int coordinateX,
            int coordinateY) {

        float radius = indicator.getRadiusPx();
        int color = indicator.getSelectedColor();
        int selectedPosition = indicator.getSelectedPosition();
        int selectingPosition = indicator.getSelectingPosition();
        int lastSelectedPosition = indicator.getLastSelectedPosition();

        if (indicator.isInteractiveAnimation()) {
            if (position == selectingPosition) {
                color = value.getColor();

            } else if (position == selectedPosition) {
                color = value.getColorReverse();
            }

        } else {
            if (position == selectedPosition) {
                color = value.getColor();

            } else if (position == lastSelectedPosition) {
                color = value.getColorReverse();
            }
        }

        fillPaint.setColor(color);
        canvas.drawCircle(coordinateX, coordinateY, radius, fillPaint);
    }
}