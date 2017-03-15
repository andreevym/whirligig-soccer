package ru.android_studio.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import static ru.android_studio.paint.DrawingView.BallActionStatus.JUMP_DOWN;
import static ru.android_studio.paint.DrawingView.BallActionStatus.JUMP_UP;
import static ru.android_studio.paint.DrawingView.BallActionStatus.WAITING;

public class DrawingView extends View {

    private static final String push = "вы набили %s очков";
    private static final String miss = "промахов %s";

    private static final int startAngleBashmak = 50;
    private static final int endAngleBashmak = 0;
    /**
     * Максимальное колличество промахов
     * по умолчанию 10
     */
    private final static int MAX_MISS_MSG = 10;
    private static final int DEFAULT_START_SPEED = 30;
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
     * Расположение объекта который пинаем по X
     */
    float ballX;
    /**
     * Расположение объекта который пинаем по Y
     */
    float ballY;
    /**
     * Угол, нужен для вращении картинки, которую пинаем
     */
    int angle;
    /**
     * При кручении запоминаем последний угол на который повернулся предмет после удара,
     * чтобы начать бить с того же места
     */
    int lastAngle;
    /**
     * На сколько предмет должен подняться вверх по Y
     */
    private float ballJumpHeightY = 10;
    /**
     * На сколько предмет должен подвинуться вправо по X
     */
    private float ballJumpHeightX = 10;
    /**
     * Колличество промахов на текущий момент
     * по умолчанию - 0
     */
    private int missCount = 0;
    /**
     * Картинка - что бьем
     */
    private Bitmap ballBitmap;
    /**
     * Картинка - чем бьем
     */
    private Bitmap footwearBitmap;
    /**
     * Картинка - Петросяно 300
     */
    private Bitmap tractoristoBitmap;
    /**
     * Картинка - бабка при проигрыше
     */
    private Bitmap babkaBitmap;
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
     * Для воспроизведении песни Газманова при проигрыше
     */
    private MediaPlayer mediaPlayer;

    /**
     * Статус объекта который пинаем на текущий момент
     */
    private BallActionStatus ballStatus;
    /**
     * Нужно чтобы объект вернулся на туже точку откуда его удалили
     * Координата по X
     */
    private float ballReturnToPoitX;
    /**
     * Нужно чтобы объект вернулся на туже точку откуда его удалили
     * Координата по Y
     */
    private float ballReturnToPoitY;
    /**
     * До какой координаты X должны двигаться.
     * наша целевая координата по X
     */
    private float ballEndX;
    /**
     * До какой координаты Y должны двигаться.
     * наша целевая координата по Y
     */
    private float ballEndY;

    public DrawingView(Context context) {
        super(context);

        ballStatus = WAITING;
        pushMsg = String.format(push, pushCount);
        missMsg = String.format(miss, missCount);
    }

    private Bitmap getFootwearByDrawable(int drawableBashmak){
        Bitmap resourceBashmak = BitmapFactory.decodeResource(getResources(), drawableBashmak);
        int bashmakWidth = resourceBashmak.getWidth() / 3;
        int bashmakHeight = resourceBashmak.getHeight() / 3;
        pathX = bashmakWidth;
        return Bitmap.createScaledBitmap(resourceBashmak, bashmakWidth, bashmakHeight, false);
    }

    private Bitmap getBallByDrawable(int drawableBall){
        Bitmap resourceBall = BitmapFactory.decodeResource(getResources(), drawableBall);
        // ballBitmap = Bitmap.createScaledBitmap(ballBitmapBitmap, ballBitmapBitmap.getWidth() / 5, ballBitmapBitmap.getHeight() / 5, false);
        return Bitmap.createScaledBitmap(resourceBall, resourceBall.getWidth() / 8, resourceBall.getHeight() / 8, false);
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

        Bitmap scaleTractoristoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tractoristo300);
        tractoristoBitmap = Bitmap.createScaledBitmap(scaleTractoristoBitmap, getWidth() - 50, getHeight() - 150, false);
        babkaBitmap = getBabkaByDrawable(R.drawable.babka);

        changeLevel();

        ballX = getWidth() / 2 - (ballBitmap.getWidth() / 2);
        ballY = getHeight() - ballBitmap.getHeight();
        ballReturnToPoitY = ballY;
        ballReturnToPoitX = ballX;

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
        // check end
        if (missCount == MAX_MISS_MSG) {
            return true;
        }

        float currentActionY = event.getY();
        float currentActionX = event.getX();

        System.out.println("Click :::::: X: %s ; Y: %s");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                boolean assert1X = this.ballX <= currentActionX;
                boolean assert2X = currentActionX <= (this.ballX + (float) ballBitmap.getWidth());

                boolean assert1Y = this.ballY <= currentActionY;
                boolean assert2Y = currentActionY <= (this.ballY + (float) ballBitmap.getHeight());

                if (assert1X && assert2X &&
                        assert1Y && assert2Y) {
                    System.out.println("УДАР!!!");
                    pushCount++;
                    changeLevel();
                    pushMsg = String.format(push, pushCount);

                    currentAngleBashmak = startAngleBashmak;

                    // calculate action by X
                    currentXBashmak = currentActionX;
                    currentYBashmak = currentActionY;

                    endXBashmak = currentActionX + pathX;

                    isBashmakInAction = true;

                    ballEndX = currentActionX + ballJumpHeightX;
                    ballEndY = currentActionY + ballJumpHeightY;
                    ballStatus = JUMP_UP;

                    printFlyPrams();
                } else {
                    missCount++;
                    missMsg = String.format(miss, missCount);
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

    private void printFlyPrams() {
        System.out.println("PrintFlyPrams");

        System.out.println("FLY PARAMS:::: ballX: " + ballX);
        System.out.println("FLY PARAMS:::: ballEndX: " + ballEndX);

        System.out.println("FLY PARAMS:::: ballY: " + ballY);
        System.out.println("FLY PARAMS:::: ballEndY: " + ballEndY);

        System.out.println("FLY PARAMS:::: ballStatus: " + ballStatus);
        System.out.println("FLY PARAMS:::: ballReturnToPoitY: " + ballReturnToPoitY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        System.out.println("currentLevel: " + currentLevel);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(34);

        // check end
        if (missCount == MAX_MISS_MSG) {
            canvas.drawText("Ты просрал", 10, 50, paint);
            canvas.save(); //Save the position of the canvas.
            canvas.drawBitmap(babkaBitmap, 10, 100, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            return;
        }

        if (ballStatus.isJumped()) {
            printFlyPrams();


            int intBallX = (int) this.ballX;
            int intBallEndX = (int) this.ballEndX;

            int intBallY = (int) this.ballY;
            int intBallEndY = (int) this.ballEndY;

            int speed = (pushCount + DEFAULT_START_SPEED) / 10 + 1;

            if (intBallEndY > intBallY && ballStatus == JUMP_UP) {
                System.out.println("Y UP");
                this.ballY -= speed;
            } else if (intBallEndY > intBallY && ballStatus == JUMP_DOWN) {
                System.out.println("Y DOWN");
                this.ballY += speed;
            }

            if (intBallEndX > intBallX && ballStatus == JUMP_UP) {
                System.out.println("X UP");
                this.ballX += speed;
            } else if (intBallEndX < intBallX && ballStatus == JUMP_DOWN) {
                System.out.println("X DOWN");
                this.ballX -= speed;
            }

            if (intBallX <= intBallEndX && ballStatus == JUMP_DOWN &&
                    intBallY >= intBallEndY && ballStatus == JUMP_DOWN) {
                ballStatus = WAITING;
                System.out.println("ALL WAITING");
            } else if (intBallX >= intBallEndX && ballStatus == JUMP_UP &&
                    intBallY <= 0 && ballStatus == JUMP_UP) {
                this.ballEndX = ballReturnToPoitX;
                this.ballEndY = ballReturnToPoitY;
                ballStatus = JUMP_DOWN;
                System.out.println("ALL JUMP_DOWN");
            }
        }

        drawBall(canvas, paint);

        // success play
        if (isBashmakInAction &&
                startAngleBashmak >= currentAngleBashmak &&
                currentAngleBashmak >= endAngleBashmak
                ) {
            drawBashmak(canvas, paint);
        }

        if (pushCount == 300) {
            canvas.drawText("Тристааа!!", 10, 50, paint);
            superPriz(canvas, paint);
        } else {
            canvas.drawText(missMsg, 10, 50, paint);
            canvas.drawText(pushMsg, 10, 100, paint);
        }

        //Call the next frame.
        invalidate();
    }

    private void superPriz(Canvas canvas, Paint paint) {
        canvas.drawText("Приз от тракториста", 10, 100, paint);
        if (tractoristoBitmap != null) {
            canvas.save(); //Save the position of the canvas.
            canvas.drawBitmap(tractoristoBitmap, 10, 150, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
        }
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

    private void drawBall(Canvas canvas, Paint paint) {
        angle = lastAngle;
        angle += pushCount;

        if (ballBitmap != null) {
            canvas.save(); //Save the position of the canvas.
            if (ballStatus.isJumped()) {
                //canvas.rotate(angle, ballX, ballY); //Rotate the canvas.
                //lastAngle = angle;
            } else {
                canvas.rotate(lastAngle, ballX, ballY);
            }
            canvas.drawBitmap(ballBitmap, ballX, ballY, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
        }
    }

    enum BallActionStatus {
        WAITING,
        JUMP_UP,
        JUMP_DOWN;

        public boolean isJumped() {
            return this == JUMP_UP || this == JUMP_DOWN;
        }
    }

    private Level currentLevel;

    private boolean changeLevel() {
        Level newLevel = Level.getLevelByPushCount(pushCount);
        if(newLevel != null) {
            changeLevel(newLevel);
            return true;
        }

        return false;
    }

    private void changeLevel(Level newLevel) {
        currentLevel = newLevel;

        ballBitmap = getBallByDrawable(currentLevel.imageBall);
        footwearBitmap = getFootwearByDrawable(currentLevel.imageFootwear);
    }

    enum Level {
        SCHOOLBOY("Уровень 1 школьник - пинает хуй", 0, R.drawable.football, R.drawable.bashmak),
        NEXT_1("NEXT", 2, R.drawable.football, R.drawable.football),
        NEXT_2("басота - босоногий мальчуган", 5, R.drawable.football, R.drawable.bosonogii),
        NEXT_3("носки", 7, R.drawable.football, R.drawable.noski),
        NEXT_4("равные кеды", 10, R.drawable.football, R.drawable.bashmak),
        NEXT_5("кеды", 14, R.drawable.football, R.drawable.bashmak),
        NEXT_6("буцы", 16, R.drawable.football, R.drawable.bashmak),
        RONALDO("Уровень 3 роналдо - золотая буца", 20, R.drawable.football, R.drawable.bashmak),
        MESSI("Уровень 4 Месси - золотой мяч", 25, R.drawable.football, R.drawable.bashmak),
        ZIDAN("Уровнеь 5 Зидан бьет головой Матерацци", 27, R.drawable.football, R.drawable.bashmak),
        UNKNOWN("UNKNOWN", -1, R.drawable.football, R.drawable.bashmak);

        final String title;
        final int pushCount;

        final int imageBall;
        final int imageFootwear;

        Level(String title, int pushCount, int imageBall, int imageFootwear) {
            this.title = title;
            this.pushCount = pushCount;
            this.imageBall = imageBall;
            this.imageFootwear = imageFootwear;
        }

        public static Level getLevelByPushCount(int pushCount) {
            for (Level level : values()) {
                if(pushCount == level.pushCount) {
                    return level;
                }
            }
            return UNKNOWN;
        }
    }
}