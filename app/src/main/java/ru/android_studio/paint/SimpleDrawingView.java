package ru.android_studio.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SimpleDrawingView extends View {

    public static final int MAX_FINGERS = 5;
    int framesPerSecond = 60;
    long animationDuration = 10000; // 10 seconds
    long startTime;
    Rect r = new Rect(10, 10, 200, 100);
    Rect rect;
    private Path[] mFingerPaths = new Path[MAX_FINGERS];
    private Paint mFingerPaint;
    private Paint textPaint;
    private ArrayList<Path> mCompletedPaths;
    private RectF mPathBounds = new RectF();
    private Matrix matrix = new Matrix();

    public SimpleDrawingView(Context context) {
        super(context);

        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompletedPaths = new ArrayList<>();

        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setColor(Color.BLACK);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeWidth(6);
        mFingerPaint.setStrokeCap(Paint.Cap.BUTT);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.RED);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1);
        textPaint.setStrokeCap(Paint.Cap.BUTT);
        textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Path completedPath : mCompletedPaths) {
            canvas.drawPath(completedPath, mFingerPaint);
            canvas.drawTextOnPath("test", completedPath, 1.0f, 23.6f, mFingerPaint);
        }

        for (Path fingerPath : mFingerPaths) {
            if (fingerPath != null) {
                canvas.drawPath(fingerPath, mFingerPaint);
            }
        }

        if (rect != null) {
            canvas.drawRect(rect, textPaint);
            drawText(canvas, 150, 150);

            // fill
            /*textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.MAGENTA);
            canvas.drawRect(r, textPaint);*/

            // border
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setColor(Color.BLACK);
            canvas.drawRect(r, textPaint);
            canvas.drawText("A", r.exactCenterX(), r.exactCenterY(), textPaint);
        }
    }

    private void drawText(Canvas canvas, int x, int y) {
        String text = "My Text";

        canvas.rotate(-45, x, y);
        canvas.drawText(text, x, y, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS) {
            mFingerPaths[id] = new Path();
            mFingerPaths[id].moveTo(event.getX(actionIndex), event.getY(actionIndex));
            rect = new Rect();
            rect.set(10, 10, 10, 10);

        } else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS) {
            mFingerPaths[id].setLastPoint(event.getX(actionIndex), event.getY(actionIndex));
            mCompletedPaths.add(mFingerPaths[id]);
            mFingerPaths[id].computeBounds(mPathBounds, true);
            invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                    (int) mPathBounds.right, (int) mPathBounds.bottom);
            mFingerPaths[id] = null;
        }

        for (int i = 0; i < cappedPointerCount; i++) {
            if (mFingerPaths[i] != null) {
                int index = event.findPointerIndex(i);
                mFingerPaths[i].lineTo(event.getX(index), event.getY(index));
                mFingerPaths[i].computeBounds(mPathBounds, true);
                invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                        (int) mPathBounds.right, (int) mPathBounds.bottom);
            }
        }

        return true;
    }
}