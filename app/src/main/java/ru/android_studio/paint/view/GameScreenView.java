package ru.android_studio.paint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import ru.android_studio.paint.R;
import ru.android_studio.paint.model.Level;
import ru.android_studio.paint.service.BallService;
import ru.android_studio.paint.service.LevelService;
import ru.android_studio.paint.service.MonitoringService;
import ru.android_studio.paint.service.ViewService;

public class GameScreenView extends View {

    private static final int startAngleBashmak = 50;
    private static final int endAngleBashmak = 0;
    /**
     * Расположение объекта который пинаем по X
     */
    private static float currentXBashmak;
    /**
     * Расположение объекта который пинаем по Y
     */
    private static float currentYBashmak;
    private static float endXBashmak;
    private static int pathX;
    private static int currentAngleBashmak = startAngleBashmak;
    private static boolean isBashmakInAction = false;
    float x, y;

    /**
     * Картинка - что бьем
     */
    private Bitmap ballBitmap;
    /**
     * Картинка - чем бьем
     */
    private Bitmap footwearBitmap;
    /**
     * Картинка - бабка при проигрыше
     */
    private Bitmap babkaBitmap;

    /**
     * Для воспроизведении песни Газманова при проигрыше
     */
    private MediaPlayer mediaPlayer;

    private LevelService levelService = new LevelService();
    private ViewService viewService = new ViewService();
    private BallService ballService = new BallService();
    private MonitoringService monitoringService = new MonitoringService();

    public GameScreenView(Context context) {
        super(context);

        ballService.init();
        monitoringService.init();
    }

    private Bitmap getBabkaByDrawable(int drawableBabka){
        Bitmap scaleBabkaBitmapBitmap = BitmapFactory.decodeResource(getResources(), drawableBabka);
        return Bitmap.createScaledBitmap(scaleBabkaBitmapBitmap, getWidth() - 50, getHeight() - 150, false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        System.out.println("Screen size:::::::::");
        System.out.println("DrawingView: H ::::: " + getHeight());
        System.out.println("DrawingView: W ::::: " + getWidth());

        // prepare images dependencies from level
        levelHandler();

        monitoringService.loadImages(getResources(), getWidth(), getHeight());
        babkaBitmap = getBabkaByDrawable(R.drawable.babka);

        ballService.load(getWidth(), getHeight(), ballBitmap.getWidth(), ballBitmap.getHeight());

        mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), R.raw.gazmanchic);

        setPadding(0, 0, 0, 0);
    }

    /**
     * Если нужно остановить музыку Газманова
     */
    protected void onClose() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (monitoringService.isMaxMissCount()) {
            return true;
        }

        float currentActionY = event.getY();
        float currentActionX = event.getX();

        System.out.println("Click :::::: X: %s ; Y: %s");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                if (ballService.isClickOnBall(currentActionX, currentActionY,
                        ballBitmap.getWidth(), ballBitmap.getHeight())) {

                    levelHandler();

                    monitoringService.pushed();
                    ballService.push(currentActionX, currentActionY);

                    currentAngleBashmak = startAngleBashmak;

                    // calculate action by X
                    currentXBashmak = currentActionX;
                    currentYBashmak = currentActionY;

                    endXBashmak = currentActionX + pathX;

                    isBashmakInAction = true;


                    ballService.printInfo();
                } else {
                    monitoringService.ballMissed();
                    isBashmakInAction = false;
                }
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                this.x = (int) currentActionX;
                this.y = (int) currentActionY;

                invalidate();
            }

            break;
            case MotionEvent.ACTION_UP:

                this.x = (int) currentActionX;
                this.y = (int) currentActionY;
                System.out.println(".................." + this.x + "......" + this.y); //x= 345 y=530
                invalidate();
                break;
        }
        return true;
    }

    private void levelHandler() {
        Level level = levelService.changeLevel(monitoringService.getPushedCount());
        ballBitmap = viewService.getBallByDrawable(level.getImageBall(), getResources());
        footwearBitmap = viewService.getFootwearByDrawable(level.getImageFootwear(), getResources());
        pathX = footwearBitmap.getWidth();
    }

    @Override
    public void onDraw(Canvas canvas) {
        System.out.println("currentLevel: " + levelService.getCurrentLevel());
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(34);

        // check end
        if (monitoringService.isMaxMissCount()) {
            canvas.drawText("Ты просрал", 10, 50, paint);
            canvas.save(); //Save the position of the canvas.
            canvas.drawBitmap(babkaBitmap, 10, 100, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            return;
        }

        if (ballService.getBallStatus().isJumped()) {
            ballService.printInfo();


            ballService.draw(monitoringService.getPushedCount());
        }

        ballService.drawBall(canvas, paint, ballBitmap, monitoringService.getPushedCount());

        // success play
        if (isBashmakInAction &&
                startAngleBashmak >= currentAngleBashmak &&
                currentAngleBashmak >= endAngleBashmak
                ) {
            drawBashmak(canvas, paint);
        }

        monitoringService.drawInfo(monitoringService.getPushedCount(), canvas, paint);

        //Call the next frame.
        invalidate();
    }

    private void drawBashmak(Canvas canvas, Paint paint) {
        if (currentXBashmak == 0 || currentYBashmak == 0) {
            System.err.printf("currentXBashmak == %s || currentYBashmak == %s", currentXBashmak, currentYBashmak);
            return;
        }

        if (currentXBashmak >= endXBashmak) {
            System.out.println("End draw bashmak");
            isBashmakInAction = false;
            return;
        }

        canvas.save(); //Save the position of the canvas.
        canvas.rotate(currentAngleBashmak, x - 150, y - 150); //Rotate the canvas.
        canvas.drawBitmap(footwearBitmap, currentXBashmak - 75, currentYBashmak - 75, paint); //Draw the ball on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.

        currentAngleBashmak -= 4;
        currentXBashmak++;
    }
}