package ru.android_studio.paint.service;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import ru.android_studio.paint.R;
import ru.android_studio.paint.model.Level;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public class MonitoringService {

    private static final String push = "вы набили %s очков";
    private static final String miss = "промахов %s";

    /**
     * Картинка - Петросяно 300
     */
    private Bitmap petrosyan300;

    /**
     * Колличество набранных очков (пинков)
     * от этого параметра зависит скорость полёта предмета который пинаешь,
     * т.к с колличеством ударов начинаешь сильнее бить
     */
    private int pushCount;

    /**
     * Текст сообщения в левом верхнем углу
     * пример: вы набрали 8 очков
     */
    private String pushMsg;

    /**
     * Текст сообщения в левом верхнем углу
     * пример: промахов 1
     */
    private String missMsg;
    /**
     * Колличество промахов на текущий момент
     * по умолчанию - 0
     */
    private int missCount = 0;
    /**
     * Максимальное колличество промахов
     * по умолчанию 10
     */
    private final static int MAX_MISS_MSG = 10;

    public void init() {
        pushMsg = String.format(push, pushCount);
        missMsg = String.format(miss, missCount);
    }

    public void ballMissed() {
        missCount++;
        missMsg = String.format(miss, missCount);
    }

    public void pushed() {
        System.out.println("УДАР!!!");

        pushCount++;
        pushMsg = String.format(push, pushCount);
    }

    public boolean isMaxMissCount() {
        return missCount == MAX_MISS_MSG;
    }

    public int getPushedCount() {
        return pushCount;
    }

    public void drawInfo(int pushedCount, Canvas canvas, Paint paint) {
        if (pushedCount == 300) {
            canvas.drawText("Тристааа!!", 10, 50, paint);
            superPriz(canvas, paint);
        } else {
            canvas.drawText(missMsg, 10, 50, paint);
            canvas.drawText(pushMsg, 10, 100, paint);
        }
    }

    private void superPriz(Canvas canvas, Paint paint) {
        canvas.drawText("Приз от тракториста", 10, 100, paint);
        if (petrosyan300 != null) {
            canvas.save(); //Save the position of the canvas.
            canvas.drawBitmap(petrosyan300, 10, 150, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
        }
    }

    public void loadImages(Resources resources, int width, int height) {
        System.out.println("Screen size:::::::::");
        System.out.println("DrawingView: H ::::: " + height);
        System.out.println("DrawingView: W ::::: " + width);

        Bitmap scaleTractoristoBitmap = BitmapFactory.decodeResource(resources, R.drawable.tractoristo300);
        petrosyan300 = Bitmap.createScaledBitmap(scaleTractoristoBitmap, width - 50, height - 150, false);
    }

    public void drawLevelInfo(float pushedCount, Level currentLevel, Canvas canvas, Paint numberLevelPaint, Paint levelPaint, Paint backgroundLevelPaint) {
        if(currentLevel == Level.END) {
            return;
        }

        Level nextLevel = currentLevel.getNextLevel();

        RectF mRect = new RectF(250, 0, 350, 100);

        float nextPushCount = nextLevel.getPushCount();
        System.out.println("pushCount: " + nextPushCount);
        float onePercentPushCount = (nextPushCount - currentLevel.getPushCount()) / 100;

        System.out.println("one Percent Push Count: " + onePercentPushCount);
        float currentLevelPastPercent = (currentLevel.getPushCount() - pushedCount) / onePercentPushCount;

        float onePercentGradus = 3.6f;
        float result = currentLevelPastPercent * onePercentGradus;

        // 360 градусов = 100 %
        // 3.6 градуса = 1%
        // 21/19 жк

        int showLevel = currentLevel.getNumber();

        canvas.drawArc(mRect, 0, 360, true, backgroundLevelPaint);
        int needPushCount = (int) (nextPushCount - pushedCount);
        if(needPushCount == 0) {
            showLevel = currentLevel.getNextLevel().getNumber();// (int) (nextLevel.getNextLevel().getPushCount() - pushedCount);
        } else {
            canvas.drawArc(mRect, 270, result, false, levelPaint);
        }
        canvas.drawText(String.valueOf(showLevel), mRect.centerX() - 18, mRect.centerY() + 15, numberLevelPaint);
    }

    public void drawLevel(Canvas canvas, Paint paint, Level currentLevel) {
        canvas.drawText(currentLevel.getTitle(), 10, 150, paint);
    }
}
