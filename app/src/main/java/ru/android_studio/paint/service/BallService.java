package ru.android_studio.paint.service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import ru.android_studio.paint.model.BallStatus;
import ru.android_studio.paint.model.Level;

/**
 * Управление мячом
 */
public class BallService {

    /**
     * Угол, нужен для вращении картинки, которую пинаем
     */
    private int angle;
    /**
     * При кручении запоминаем последний угол на который повернулся предмет после удара,
     * чтобы начать бить с того же места
     */
    private int lastAngle;

    /**
     * Расположение объекта который пинаем по X
     */
    float ballX;
    /**
     * Расположение объекта который пинаем по Y
     */
    float ballY;

    /**
     * На сколько предмет должен подняться вверх по Y
     */
    private float ballJumpHeightY = 10;
    /**
     * На сколько предмет должен подвинуться вправо по X
     */
    private float ballJumpHeightX = 10;

    /**
     * Статус объекта который пинаем на текущий момент
     */
    private BallStatus ballStatus;
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

    /**
     * Картинка - что бьем
     */
    private Bitmap ballBitmap;

    public void init() {
        ballStatus = BallStatus.WAITING;
    }

    public void drawBall(Canvas canvas, Paint paint, Bitmap ballBitmap, int pushedCount) {
        angle = lastAngle;
        angle += pushedCount;

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

    public void load(int width, int height){
        ballX = width / 2 - (ballBitmap.getWidth() / 2);
        ballY = height - ballBitmap.getHeight();
        ballReturnToPoitY = ballY;
        ballReturnToPoitX = ballX;
    }

    public BallStatus getBallStatus() {
        return ballStatus;
    }

    public void push(float clickX, float clickY) {
        ballEndX = clickX + ballJumpHeightX;
        ballEndY = clickY + ballJumpHeightY;
        ballStatus = BallStatus.JUMP_UP;
    }


    public boolean isClickOnBall(float clickX, float clickY) {
        boolean assert1X = this.ballX <= clickX;
        boolean assert2X = clickX <= (this.ballX + (float) ballBitmap.getWidth());

        boolean assert1Y = this.ballY <= clickY;
        boolean assert2Y = clickY <= (this.ballY + (float) ballBitmap.getHeight());
        return assert1X && assert2X && assert1Y && assert2Y;
    }

    public void printInfo() {
        System.out.println("PrintFlyPrams");

        System.out.println("FLY PARAMS:::: ballX: " + ballX);
        System.out.println("FLY PARAMS:::: ballEndX: " + ballEndX);

        System.out.println("FLY PARAMS:::: ballY: " + ballY);
        System.out.println("FLY PARAMS:::: ballEndY: " + ballEndY);

        System.out.println("FLY PARAMS:::: ballActionStatus: " + ballStatus);
        System.out.println("FLY PARAMS:::: ballReturnToPoitY: " + ballReturnToPoitY);
    }

    private SpeedService speedService = new SpeedService();

    public void draw(int pushedCount) {
        int intBallX = (int) this.ballX;
        int intBallEndX = (int) this.ballEndX;

        int intBallY = (int) this.ballY;
        int intBallEndY = (int) this.ballEndY;

        int speed = speedService.getSpeed(pushedCount, ballStatus);

        if (intBallEndY > intBallY && ballStatus == BallStatus.JUMP_UP) {
            System.out.println("Y UP");
            this.ballY -= speed;
        } else if (intBallEndY > intBallY && ballStatus == BallStatus.JUMP_DOWN) {
            System.out.println("Y DOWN");
            this.ballY += speed;
        }

        if (intBallEndX > intBallX && ballStatus == BallStatus.JUMP_UP) {
            System.out.println("X UP");
            this.ballX += speed;
        } else if (intBallEndX < intBallX && ballStatus == BallStatus.JUMP_DOWN) {
            System.out.println("X DOWN");
            this.ballX -= speed;
        }

        if (intBallX <= intBallEndX && ballStatus == BallStatus.JUMP_DOWN &&
                intBallY >= intBallEndY && ballStatus == BallStatus.JUMP_DOWN) {
            ballStatus = BallStatus.WAITING;
            System.out.println("ALL BallStatus.WAITING");
        } else if (intBallX >= intBallEndX && ballStatus == BallStatus.JUMP_UP &&
                intBallY <= 0 && ballStatus == BallStatus.JUMP_UP) {
            this.ballEndX = ballReturnToPoitX;
            this.ballEndY = ballReturnToPoitY;
            ballStatus = BallStatus.JUMP_DOWN;
            System.out.println("ALL BallStatus.JUMP_DOWN");
        }
    }

    public void setBallBitmap(Bitmap ballBitmap) {
        this.ballBitmap = ballBitmap;
    }

    public Bitmap getBallBitmap() {
        return ballBitmap;
    }
}
