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

    private static final int STROKE_WIDTH = 12;
    float pushX = -1f;
    float pushY = -1f;
    float x, y;
    private boolean isPushDebugEnable = true;
    private LevelService levelService = new LevelService();
    private ViewService viewService = new ViewService();
    private BallService ballService = new BallService();
    private MonitoringService monitoringService = new MonitoringService();
    private FootwearService footwearService = new FootwearService();
    private EndGameService endGameService = new EndGameService();
    private int width;
    private int height;

    public GameScreenView(Context context, int width, int height) {
        super(context);

        this.width = width;
        this.height = height;

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

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                if (ballService.isClickOnBall(clickX, clickY)) {
                    pushX = clickX;
                    pushY = clickY;
                    levelHandler();

                    monitoringService.pushed();
                    ballService.push(clickX, clickY);

                    footwearService.push(clickX, clickY);
                    ballService.printInfo();
                } else {
                    monitoringService.ballMissed();
                    footwearService.missed();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                this.x = (int) clickX;
                this.y = (int) clickY;
                break;
            }
            case MotionEvent.ACTION_UP: {
                this.x = (int) clickX;
                this.y = (int) clickY;
                break;
            }
        }
        invalidate();
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
        Paint levelPaint = makeLevelPaint();
        Paint backgroundLevelPaint = makeBackgroundLevelPaint();
        Paint numberLevelPaint = makeNumberLevelPaint();

        // Проверка окончания игры - Поражение
        if (monitoringService.isMaxMissCount()) {
            endGameService.show(canvas, paint);
            return;
        }

        // Проверка окончания игры - Победа
        if (levelService.getCurrentLevel() == Level.END) {
            endGameService.win(canvas, paint);
            return;
        }

        // если мячик прыгает
        if (!isPushDebugEnable) {
            if (ballService.getBallStatus().isJumped()) {
                ballService.printInfo();
                ballService.draw(monitoringService.getPushedCount());
            }
        }

        ballService.drawBall(canvas, paint, ballService.getBallBitmap(), monitoringService.getPushedCount());

        // рисуем удар ботинком
        if (!isPushDebugEnable) {
            footwearService.draw(canvas, paint, this.x, this.y);
        }
        // рисуем информациб об игре
        monitoringService.drawInfo(monitoringService.getPushedCount(), canvas, paint);
        monitoringService.drawLevel(canvas, paint, levelService.getCurrentLevel());

        monitoringService.drawLevelInfo(monitoringService.getPushedCount(), levelService.getCurrentLevel(), canvas, numberLevelPaint, levelPaint, backgroundLevelPaint);


        if (isPushDebugEnable) {
            Paint debugPushPointPaint = makeDebugPushPointPaint();
            if (pushX != -1f && pushY != -1f) {
                canvas.drawPoint(pushX, pushY, debugPushPointPaint);
                float cx = ballService.getBallCX();
                float cy = ballService.getBallCY();
                // нарисовать центр
                canvas.drawPoint(cx, cy, debugPushPointPaint);

                float diffX = cx - pushX;
                float diffY = cy - pushY;

                float tangF = diffY / diffX;
                if (tangF > 0) {
                    tangF *= -1;
                }

                canvas.drawText("diffX: " + diffX, 10, 250, paint);
                canvas.drawText("diffY: " + diffY, 10, 300, paint);
                canvas.drawText("tangF: " + tangF, 10, 350, paint);
                canvas.drawText("cx: " + cx, 10, 400, paint);
                canvas.drawText("cy: " + cy, 10, 450, paint);
                canvas.drawText("pushX: " + pushX, 10, 500, paint);
                canvas.drawText("pushY: " + pushY, 10, 550, paint);

                canvas.drawText("maxX: " + canvas.getWidth(), 10, 600, paint);
                canvas.drawText("maxY: " + canvas.getHeight(), 10, 650, paint);

                float prevX = cx + diffX;
                float prevY = cy + diffY;
                int rightBackX = 0;
                int leftBackX = 0;
                int upY = 0;
                int downY = 0;
                for (int i = 0; i < 3500; i++) {
                    float nextX;
                    float nextY;

                    //направление полёта по горизонтали
                    if (diffX < 0) {
                        if (rightBackX > 0) {
                            nextX = prevX + 1;
                        } else if (leftBackX > 0) {
                            nextX = prevX + 1;
                        } else {
                            nextX = prevX - 1;
                        }
                    } else {
                        if (rightBackX > 0) {
                            nextX = prevX - 1;
                        } else if (leftBackX > 0) {
                            nextX = prevX + 1;
                        } else {
                            nextX = prevX + 1;
                        }
                    }

                    if (nextX > width) {
                        rightBackX = i;
                        nextX -= 2;
                    } else if (nextX < 0) {
                        leftBackX = i;
                        nextX += 2;
                    }

                    //направление полёта по вертикали
                    if (diffY < 0) {
                        nextY = 0;//prevY + tangF;

//                        if (downY > 0) {
//                            nextY = prevY - tangF;
//                        }
//
//                        if (upY > 0) {
//                            nextY = prevY + tangF;
//                        }
                    } else {
                        nextY = prevY - tangF;

                        if (downY > 0) {
                            nextY = prevY + tangF;
                        }

                        if (upY > 0) {
                            nextY = prevY - tangF;
                        }
                    }

                    if (nextY > height) {
                        downY = i;
                        upY = 0;
                    } else if (nextY < 0) {
                        upY = i;
                        downY = 0;
                    }

                    // Draw
                    canvas.drawPoint(nextX, nextY, debugPushPointPaint);

                    // After draw
                    prevX = nextX;
                    prevY = nextY;
                }
                // нарисовать направление полёта
                canvas.drawPoint(cx + diffX, cy + diffY, debugPushPointPaint);
            }
        }

        // Call the next frame.
        invalidate();
    }

    //German
    private Paint makeBackgroundLevelPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setTextSize(34);
        return paint;
    }

    private Paint makeNumberLevelPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setTextSize(34);
        return paint;
    }

    @NonNull
    private Paint makePaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(34);
        return paint;
    }

    @NonNull
    private Paint makeLevelPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setTextSize(34);
        return paint;
    }

    @NonNull
    private Paint makeDebugPushPointPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setTextSize(50);
        return paint;
    }
}