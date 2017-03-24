package ru.android_studio.paint.model;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public enum BallActionStatus {
    WAITING,
    JUMP_UP,
    JUMP_DOWN;

    public boolean isJumped() {
        return this == JUMP_UP || this == JUMP_DOWN;
    }
}
