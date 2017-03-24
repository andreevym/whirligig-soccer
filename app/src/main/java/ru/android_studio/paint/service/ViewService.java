package ru.android_studio.paint.service;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public class ViewService {

    public Bitmap getFootwearByDrawable(int drawableBashmak, Resources resources){
        Bitmap resourceBashmak = BitmapFactory.decodeResource(resources, drawableBashmak);
        int bashmakWidth = resourceBashmak.getWidth() / 3;
        int bashmakHeight = resourceBashmak.getHeight() / 3;
        return Bitmap.createScaledBitmap(resourceBashmak, bashmakWidth, bashmakHeight, false);
    }

    public Bitmap getBallByDrawable(int drawableBall, Resources resources){
        Bitmap resourceBall = BitmapFactory.decodeResource(resources, drawableBall);
        // ballBitmap = Bitmap.createScaledBitmap(ballBitmapBitmap, ballBitmapBitmap.getWidth() / 5, ballBitmapBitmap.getHeight() / 5, false);
        return Bitmap.createScaledBitmap(resourceBall, resourceBall.getWidth() / 8, resourceBall.getHeight() / 8, false);
    }
}
