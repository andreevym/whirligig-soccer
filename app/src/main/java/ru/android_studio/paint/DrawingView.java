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

import static ru.android_studio.paint.DrawingView.XuiActionStatus.JUMP_DOWN;
import static ru.android_studio.paint.DrawingView.XuiActionStatus.JUMP_UP;
import static ru.android_studio.paint.DrawingView.XuiActionStatus.WAITING;

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
    float xuiX;

    /**
     * Расположение объекта который пинаем по Y
     */
    float xuiY;

    /**
     * Угол, нужен для вращении картинки, которую пинаем
     */
    int angle;

    /**
     * На сколько предмет должен подняться вверх по Y
     */
    private float xuiJumpHeightY = 10;
    /**
     * На сколько предмет должен подвинуться вправо по X
     */
    private float xuiJumpHeightX = 10;

    /**
     * При кручении запоминаем последний угол на который повернулся предмет после удара,
     * чтобы начать бить с того же места
     */
    int lastAngle;

    /**
     * Колличество промахов на текущий момент
     * по умолчанию - 0
     */
    private int missCount = 0;
    /**
     * Картинка - что бьем
     */
    private Bitmap xuiBitmap;
    /**
     * Картинка - чем бьем
     */
    private Bitmap bashmakBitmap;
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
    private static final int DEFAULT_START_SPEED = 30;

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
    private XuiActionStatus xuiStatus;
    /**
     * Нужно чтобы объект вернулся на туже точку откуда его удалили
     * Координата по X
     */
    private float xuiReturnToPoitX;
    /**
     * Нужно чтобы объект вернулся на туже точку откуда его удалили
     * Координата по Y
     */
    private float xuiReturnToPoitY;
    /**
     * До какой координаты X должны двигаться.
     * наша целевая координата по X
     */
    private float xuiEndX;
    /**
     * До какой координаты Y должны двигаться.
     * наша целевая координата по Y
     */
    private float xuiEndY;

    public DrawingView(Context context) {
        super(context);

        xuiStatus = WAITING;
        pushMsg = String.format(push, pushCount);
        missMsg = String.format(miss, missCount);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        System.out.println("DrawingView: H ::::: " + getHeight());
        System.out.println("DrawingView: W ::::: " + getWidth());

        Bitmap scaleTractoristoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tractoristo300);
        tractoristoBitmap = Bitmap.createScaledBitmap(scaleTractoristoBitmap, getWidth() - 50, getHeight() - 150, false);

        Bitmap scaleBabkaBitmapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.babka);
        babkaBitmap = Bitmap.createScaledBitmap(scaleBabkaBitmapBitmap, getWidth() - 50, getHeight() - 150, false);

        Bitmap scaleBashmakBitmapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bashmak);
        int bashmakWidth = scaleBashmakBitmapBitmap.getWidth() / 3;
        int bashmakHeight = scaleBashmakBitmapBitmap.getHeight() / 3;
        bashmakBitmap = Bitmap.createScaledBitmap(scaleBashmakBitmapBitmap, bashmakWidth, bashmakHeight, false);
        pathX = bashmakWidth;

        // FIXME: Bitmap xuiBitmapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xui);
        Bitmap xuiBitmapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.football);
        // xuiBitmap = Bitmap.createScaledBitmap(xuiBitmapBitmap, xuiBitmapBitmap.getWidth() / 5, xuiBitmapBitmap.getHeight() / 5, false);
        xuiBitmap = Bitmap.createScaledBitmap(xuiBitmapBitmap, xuiBitmapBitmap.getWidth() / 8, xuiBitmapBitmap.getHeight() / 8, false);

        xuiX = getWidth() / 2 - (xuiBitmap.getWidth() / 2);
        xuiY = getHeight() - xuiBitmap.getHeight();
        xuiReturnToPoitY = xuiY;
        xuiReturnToPoitX = xuiX;

        mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), R.raw.gazmanchic);

        setPadding(0, 0, 0, 0);
    }

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

                boolean assert1X = this.xuiX <= currentActionX;
                boolean assert2X = currentActionX <= (this.xuiX + (float) xuiBitmap.getWidth());

                boolean assert1Y = this.xuiY <= currentActionY;
                boolean assert2Y = currentActionY <= (this.xuiY + (float) xuiBitmap.getHeight());

                if (assert1X && assert2X &&
                        assert1Y && assert2Y) {
                    System.out.println("УДАР!!!");
                    pushCount++;
                    pushMsg = String.format(push, pushCount);

                    currentAngleBashmak = startAngleBashmak;

                    // calculate action by X
                    currentXBashmak = currentActionX;
                    currentYBashmak = currentActionY;

                    endXBashmak = currentActionX + pathX;

                    isBashmakInAction = true;

                    xuiEndX = currentActionX + xuiJumpHeightX;
                    xuiEndY = currentActionY + xuiJumpHeightY;
                    xuiStatus = JUMP_UP;

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

        System.out.println("FLY PARAMS:::: xuiX: " + xuiX);
        System.out.println("FLY PARAMS:::: xuiEndX: " + xuiEndX);

        System.out.println("FLY PARAMS:::: xuiY: " + xuiY);
        System.out.println("FLY PARAMS:::: xuiEndY: " + xuiEndY);

        System.out.println("FLY PARAMS:::: xuiStatus: " + xuiStatus);
        System.out.println("FLY PARAMS:::: xuiReturnToPoitY: " + xuiReturnToPoitY);
    }

    @Override
    public void onDraw(Canvas canvas) {
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

        if (xuiStatus.isJumped()) {
            printFlyPrams();


            int intXuiX = (int) this.xuiX;
            int intXuiEndX = (int) this.xuiEndX;

            int intXuiY = (int) this.xuiY;
            int intXuiEndY = (int) this.xuiEndY;

            int speed = (pushCount + DEFAULT_START_SPEED)/ 10 + 1;

            if (intXuiEndY > intXuiY && xuiStatus == JUMP_UP) {
                System.out.println("Y UP");
                this.xuiY -= speed;
            } else if (intXuiEndY > intXuiY && xuiStatus == JUMP_DOWN) {
                System.out.println("Y DOWN");
                this.xuiY += speed;
            }

            if (intXuiEndX > intXuiX && xuiStatus == JUMP_UP) {
                System.out.println("X UP");
                this.xuiX += speed;
            } else if (intXuiEndX < intXuiX && xuiStatus == JUMP_DOWN) {
                System.out.println("X DOWN");
                this.xuiX -= speed;
            }

            if (intXuiX <= intXuiEndX && xuiStatus == JUMP_DOWN &&
                    intXuiY >= intXuiEndY && xuiStatus == JUMP_DOWN) {
                xuiStatus = WAITING;
                System.out.println("ALL WAITING");
            } else if (intXuiX >= intXuiEndX && xuiStatus == JUMP_UP &&
                    intXuiY <= 0 && xuiStatus == JUMP_UP) {
                this.xuiEndX = xuiReturnToPoitX;
                this.xuiEndY = xuiReturnToPoitY;
                xuiStatus = JUMP_DOWN;
                System.out.println("ALL JUMP_DOWN");
            }
        }

        drawXui(canvas, paint);

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
        canvas.drawBitmap(bashmakBitmap, currentXBashmak - 75, currentYBashmak - 75, paint); //Draw the ball on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.

        currentAngleBashmak -= 4;
        currentXBashmak++;
    }

    private void drawXui(Canvas canvas, Paint paint) {
        angle = lastAngle;
        angle += pushCount;

        if (xuiBitmap != null) {
            canvas.save(); //Save the position of the canvas.
            if (xuiStatus.isJumped()) {
                //canvas.rotate(angle, xuiX, xuiY); //Rotate the canvas.
                //lastAngle = angle;
            } else {
                canvas.rotate(lastAngle, xuiX, xuiY);
            }
            canvas.drawBitmap(xuiBitmap, xuiX, xuiY, paint); //Draw the ball on the rotated canvas.
            canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.
        }
    }

    enum XuiActionStatus {
        WAITING,
        JUMP_UP,
        JUMP_DOWN;

        public boolean isJumped() {
            return this == JUMP_UP || this == JUMP_DOWN;
        }
    }
}