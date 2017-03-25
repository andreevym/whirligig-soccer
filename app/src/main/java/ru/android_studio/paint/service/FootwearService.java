package ru.android_studio.paint.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by yuryandreev on 25/03/2017.
 */

public class FootwearService {
    private static int pathX;
    /**
     * Картинка - чем бьем
     */
    private Bitmap footwearBitmap;

    private static final int startAngleBashmak = 50;
    private static final int endAngleBashmak = 0;
    private static int currentAngleBashmak = startAngleBashmak;
    private static boolean isBashmakInAction = false;
    /**
     * Расположение объекта который пинаем по X
     */
    private static float currentXFootwear;
    /**
     * Расположение объекта который пинаем по Y
     */
    private static float currentYFootwear;
    private static float endXBashmak;

    public void missed() {
        isBashmakInAction = false;
    }

    public void push(float clickX, float clickY) {
        currentAngleBashmak = startAngleBashmak;

        // calculate action by X
        currentXFootwear = clickX;
        currentYFootwear = clickY;

        endXBashmak = clickX + pathX;

        isBashmakInAction = true;
    }

    public void setFootwearBitmap(Bitmap footwearBitmap) {
        this.footwearBitmap = footwearBitmap;
        pathX = this.footwearBitmap.getWidth();
    }

    public void draw(Canvas canvas, Paint paint, float x, float y) {
        // success play
        if (isBashmakInAction &&
                startAngleBashmak >= currentAngleBashmak &&
                currentAngleBashmak >= endAngleBashmak
                ) {
            drawFootwear(canvas, paint, x, y);
        }
    }

    private void drawFootwear(Canvas canvas, Paint paint, float x, float y) {
        if (currentXFootwear == 0 || currentYFootwear == 0) {
            System.err.printf("currentXFootwear == %s || currentYFootwear == %s", currentXFootwear, currentYFootwear);
            return;
        }

        if (currentXFootwear >= endXBashmak) {
            System.out.println("End draw bashmak");
            isBashmakInAction = false;
            return;
        }

        canvas.save(); //Save the position of the canvas.
        canvas.rotate(currentAngleBashmak, x - 150, y - 150); //Rotate the canvas.
        canvas.drawBitmap(footwearBitmap, currentXFootwear - 75, currentYFootwear - 75, paint); //Draw the ball on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.

        currentAngleBashmak -= 4;
        currentXFootwear++;
    }
}
