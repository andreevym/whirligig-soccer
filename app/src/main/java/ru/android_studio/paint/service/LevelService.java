package ru.android_studio.paint.service;

import ru.android_studio.paint.model.Level;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public class LevelService {

    private static Level currentLevel = Level.LEVEL_1;

    public Level getCurrentLevel() {
        System.out.println("getCurrentLevel");
        return currentLevel;
    }

    public Level changeLevel(int pushCount) {
        System.out.println("changeLevel by push count");
        Level newLevel = getLevelByPushCount(pushCount);
        setCurrentLevel(newLevel);
        return currentLevel;
    }

    public Level getLevelByPushCount(int pushCount) {
        System.out.println("getLevelByPushCount");

        for (Level level : Level.values()) {
            if(level.getPushCount() > currentLevel.getPushCount() &&
                pushCount == level.getPushCount()) {
                return level;
            }
        }
        return currentLevel;
    }

    public void setCurrentLevel(Level newLevel) {
        if(newLevel == null) {
            LevelService.currentLevel = Level.UNKNOWN;
            System.out.println("::::: UNKNOWN Level :::::");
            return;
        } else if (newLevel == currentLevel) {
            return;
        }

        LevelService.currentLevel = newLevel;
        System.out.println("::::: NEW Level ::::: " + currentLevel);
    }
}
