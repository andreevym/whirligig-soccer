package ru.android_studio.paint;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import static ru.android_studio.paint.R.attr.alpha;

/**
 * This is demo code to accompany the Mobiletuts+ tutorial series:
 * - Android SDK: Create a Drawing App
 * 
 * Sue Smith
 * August 2013
 *
 */
public class MainActivity extends Activity implements View.OnClickListener {

	private Paint paint = new Paint();
	private int val = 255;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleDrawingView view = new SimpleDrawingView(this);
		setContentView(view);
	}

	@Override
	public void onClick(View view) {
		int ab = Color.argb(alpha, 0, 0, val);
		paint.setColor(ab);
	}
}
