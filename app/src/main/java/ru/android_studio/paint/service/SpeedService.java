package ru.android_studio.paint.service;

public class SpeedService {

    private static final int DEFAULT_START_SPEED = 50;

    private int speed;
    private int lastPushedCount;

    public int getSpeed(int pushedCount) {
        if(lastPushedCount == pushedCount) {
            // was pushed
            return speed;
        } else {
            speed = (pushedCount + DEFAULT_START_SPEED) / 10 + 1;
            lastPushedCount = pushedCount;
        }

        return speed;
    }

}
