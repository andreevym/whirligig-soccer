package ru.android_studio.paint.activity;

import android.app.Activity;
import android.os.Bundle;

import ru.android_studio.paint.view.DrawingView;

/**
 * This is demo code to accompany the Mobiletuts+ tutorial series:
 * - Android SDK: Create a Drawing App
 * <p>
 * Sue Smith
 * August 2013
 */
public class GameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawingView dv = new DrawingView(this);
        setContentView(dv);
    }
}
