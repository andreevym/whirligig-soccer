package ru.android_studio.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SimpleDrawingView extends View {

    // setup initial color
    private final int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;

    // Store circles to draw each time the user touches down
    private List<Point> circlePoints;

    public SimpleDrawingView(Context context) {
        super(context);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        // same as before
        circlePoints = new ArrayList<>();

        Button color = new Button(context);
        color.setText("Цвет");
        Button clear = new Button(context);
        clear.setWidth(100);
        clear.setText("Очистить");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Point p : circlePoints) {
            canvas.drawCircle(p.x, p.y, 5, drawPaint);
        }
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
        invalidate();
        return true;
    }
}