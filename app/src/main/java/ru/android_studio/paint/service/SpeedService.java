package ru.android_studio.paint.service;

import ru.android_studio.paint.model.BallStatus;

public class SpeedService {

    // скорость
    private static final int DEFAULT_START_SPEED = 50;

    // ускорение во время удара по мячу
    private final static int A_START_PUSH_ACTION = 5;
    private final static int A_END_PUSH_ACTION = 1;

    // ускорение
    private int a = A_START_PUSH_ACTION;

    // длительность ускорения A шаков - далее ускореение становится по умолчанию
    // private int stepA ;

    // скорость
    private int speed;

    private static final int DEFAULT_WAIT_TIME = 6;
    private int waitTime = 6;
    private int endWaitTime = 1;

    // последнее колличество ударов по мячу
    private int lastPushedCount;
    private BallStatus lastBallStatus;

    // получить скорость полёта мяча по колличеству ударов
    public int getSpeed(int pushedCount, BallStatus ballStatus) {
        if(lastPushedCount == pushedCount && ballStatus == BallStatus.JUMP_UP) {
            // если мяч уже летит после удара
            if(a <= A_START_PUSH_ACTION && a > A_END_PUSH_ACTION) {
                waitTime--;
                if(waitTime == endWaitTime) {
                    a--;
                    waitTime = DEFAULT_WAIT_TIME;
                }
            }
            return ((lastPushedCount + DEFAULT_START_SPEED) / 10 + 1) * a;
        } else if(lastPushedCount == pushedCount && ballStatus == BallStatus.JUMP_DOWN) {
            if (lastBallStatus == BallStatus.JUMP_UP) {
                a = A_START_PUSH_ACTION;
            }

            // если мяч уже летит после удара
            if(a <= A_START_PUSH_ACTION && a > A_END_PUSH_ACTION) {
                waitTime--;
                if(waitTime == endWaitTime) {
                    a--;
                    waitTime = DEFAULT_WAIT_TIME;
                }
            }
            speed = ((lastPushedCount + DEFAULT_START_SPEED) / 10 + 1) * a;
        } else {
            a = A_START_PUSH_ACTION;
            speed = ((lastPushedCount + DEFAULT_START_SPEED) / 10 + 1) * a;
        }
        // мяч стоял на месте и его только ударили
        lastPushedCount = pushedCount;
        lastBallStatus = ballStatus;
        return speed;
    }
}
