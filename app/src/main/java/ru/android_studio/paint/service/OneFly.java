package ru.android_studio.paint.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class OneFly {

    private float diffX;
    private float pushX;
    private float diffY;
    private float pushY;
    private float tangF;
    private float prevX;
    private float prevY;
    private double height;
    private double width;
    private int rightBackX;
    private int leftBackX;
    private int upY;
    private int downY;

    /**
     * Вызывать в момент загрузки страницы, когда мы знаем параметры экрана
     *
     * @param height
     * @param width
     */
    public OneFly(double height, double width) {
        this.height = height;
        this.width = width;
    }

    /**
     * Вызывать в момент удара
     *
     * @param cx
     * @param cy
     * @param pushX
     * @param pushY
     */
    public void push(float cx, float cy, float pushX, float pushY) {
        this.pushX = pushX;
        this.pushY = pushY;

        if (pushX != -1f && pushY != -1f) {
            diffX = cx - pushX;
            diffY = cy - pushY;

            tangF = diffY / diffX;
            if (tangF > 0) {
                tangF *= -1;
            }

            prevX = cx;
            prevY = cy;
        }
    }

    /**
     * Вызывать в процессе рисования
     */
    public void fly(Canvas canvas, Paint paint, Bitmap ballBitmap) {
        canvas.drawPoint(pushX, pushY, paint);
        int i = 40;
        float nextX;
        float nextY;

        //направление полёта по горизонтали
        int speed = 30;
        if (diffX < 0) {
            if (rightBackX > 0) {
                nextX = prevX + speed;
                canvas.drawText("nextX: right", 10, 450, paint);
            } else if (leftBackX > 0) {
                nextX = prevX - speed;
                canvas.drawText("nextX: left", 10, 450, paint);
            } else {
                nextX = prevX - speed;
                canvas.drawText("e nextX: left", 10, 450, paint);
            }
        } else {
            if (leftBackX > 0) {
                nextX = prevX - speed;
                canvas.drawText("nextX: left", 10, 450, paint);
            } else if (rightBackX > 0) {
                nextX = prevX + speed;
                canvas.drawText("nextX: right", 10, 450, paint);
            } else {
                nextX = prevX + speed;
                canvas.drawText("e nextX: right", 10, 450, paint);
            }
        }

        if (nextX > width) {
            leftBackX = i;
            rightBackX = 0;
            nextX = prevX;
            canvas.drawText("nextX: ANOTHER WAY", 10, 550, paint);
        } else if (nextX < 0) {
            rightBackX = i;
            leftBackX = 0;
            nextX = prevX;
            canvas.drawText("nextX: ANOTHER WAY", 10, 550, paint);
        }

        //направление полёта по вертикали
        if (diffY < 0) {
            nextY = prevY + tangF;

            if (upY > 0) {
                nextY = prevY + tangF;
                canvas.drawText("nextY: UP", 10, 650, paint);
            }

            if (downY > 0) {
                nextY = prevY - tangF;
                canvas.drawText("nextY: DOWN", 10, 650, paint);
            }
        } else {
            nextY = prevY - tangF;

            if (upY > 0) {
                nextY = prevY + tangF;
                canvas.drawText("nextY: UP", 10, 650, paint);
            }

            if (downY > 0) {
                nextY = prevY - tangF;
                canvas.drawText("nextY: DOWN", 10, 650, paint);
            }
        }

        if (nextY > height) {
            upY = i;
            downY = 0;
            nextY = prevY;
            canvas.drawText("nextY: ANOTHER WAY", 10, 750, paint);
        } else if (nextY < 0) {
            downY = i;
            upY = 0;
            nextY = prevY;
            canvas.drawText("nextY: ANOTHER WAY", 10, 750, paint);
        }

        // Draw
        canvas.drawBitmap(ballBitmap, nextX, nextY, paint);
        canvas.drawText("nextX: " + nextX, 10, 250, paint);
        canvas.drawText("nextY: " + nextY, 10, 350, paint);
        // After draw
        prevX = nextX;
        prevY = nextY;
    }
}
