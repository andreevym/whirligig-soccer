package ru.android_studio.paint.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import ru.android_studio.paint.model.Level;
import ru.android_studio.paint.service.BallService;
import ru.android_studio.paint.service.EndGameService;
import ru.android_studio.paint.service.FootwearService;
import ru.android_studio.paint.service.LevelService;
import ru.android_studio.paint.service.MonitoringService;
import ru.android_studio.paint.service.ViewService;

public class GameScreenView extends View {

    float x, y;

    private LevelService levelService = new LevelService();
    private ViewService viewService = new ViewService();
    private BallService ballService = new BallService();
    private MonitoringService monitoringService = new MonitoringService();
    private FootwearService footwearService = new FootwearService();
    private EndGameService endGameService = new EndGameService();

    public GameScreenView(Context context) {
        super(context);

        ballService.init();
        monitoringService.init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // prepare images dependencies from levelHandler
        levelHandler();

        monitoringService.loadImages(getResources(), getWidth(), getHeight());
        endGameService.load(getContext().getApplicationContext(), getResources(), getWidth(), getHeight());
        ballService.load(getWidth(), getHeight());

        setPadding(0, 0, 0, 0);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (monitoringService.isMaxMissCount()) {
            return true;
        }

        float clickX = event.getX();
        float clickY = event.getY();

        System.out.println("Click :::::: X: %s ; Y: %s");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                if (ballService.isClickOnBall(clickX, clickY)) {

                    levelHandler();

                    monitoringService.pushed();
                    ballService.push(clickX, clickY);

                    footwearService.push(clickX, clickY);
                    ballService.printInfo();
                } else {
                    monitoringService.ballMissed();
                    footwearService.missed();
                }
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                this.x = (int) clickX;
                this.y = (int) clickY;

                invalidate();
            }

            break;
            case MotionEvent.ACTION_UP:

                this.x = (int) clickX;
                this.y = (int) clickY;
                System.out.println(".................." + this.x + "......" + this.y); //x= 345 y=530
                invalidate();
                break;
        }
        return true;
    }

    private void levelHandler() {
        Level level = levelService.changeLevel(monitoringService.getPushedCount());
        ballService.setBallBitmap(viewService.getBallByDrawable(level.getImageBall(), getResources()));
        footwearService.setFootwearBitmap(viewService.getFootwearByDrawable(level.getImageFootwear(), getResources()));
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = makePaint();

        // Проверка окончания игры
        if (monitoringService.isMaxMissCount()) {
            endGameService.show(canvas, paint);
            return;
        }

        // если мячик прыгает
        if (ballService.getBallStatus().isJumped()) {
            ballService.printInfo();
            ballService.draw(monitoringService.getPushedCount());
        }

        ballService.drawBall(canvas, paint, ballService.getBallBitmap(), monitoringService.getPushedCount());

        // рисуем удар ботинком
        footwearService.draw(canvas, paint, this.x, this.y);

        // рисуем информациб об игре
        monitoringService.drawInfo(monitoringService.getPushedCount(), canvas, paint);

        // Call the next frame.
        invalidate();
    }

    @NonNull
    private Paint makePaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(34);
        return paint;
    }
}