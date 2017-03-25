package ru.android_studio.paint.service;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import ru.android_studio.paint.R;

/**
 * Created by yuryandreev on 25/03/2017.
 */

public class EndGameService {
    /**
     * Картинка - бабка при проигрыше
     */
    private Bitmap babkaBitmap;

    private AudioService audioService = new AudioService();

    private Bitmap getBabkaByDrawable(int drawableBabka, Resources resources, int width, int height) {
        Bitmap scaleBabkaBitmapBitmap = BitmapFactory.decodeResource(resources, drawableBabka);
        return Bitmap.createScaledBitmap(scaleBabkaBitmapBitmap, width - 50, height - 150, false);
    }

    public void load(Context applicationContext, Resources resources, int width, int height) {
        babkaBitmap = getBabkaByDrawable(R.drawable.babka, resources, width, height);
        audioService.load(applicationContext);
    }

    public void show(Canvas canvas, Paint paint) {
        canvas.drawText("Ты просрал", 10, 50, paint);
        canvas.save(); //Save the position of the canvas.
        canvas.drawBitmap(babkaBitmap, 10, 100, paint); //Draw the ball on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
        audioService.start();
    }
}