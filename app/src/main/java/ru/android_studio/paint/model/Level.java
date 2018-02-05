package ru.android_studio.paint.model;

import ru.android_studio.paint.R;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public enum Level {
    //SCHOOLBOY("Школьник", 0, R.drawable.football, R.drawable.bashmak),
    LEVEL_1(1, "Босоногий мальчуган", 3, R.drawable.football, R.drawable.bashmak),
    LEVEL_2(2, "Носки", 7, R.drawable.football, R.drawable.bashmak),
    LEVEL_3(3, "Равные кеды", 10, R.drawable.football, R.drawable.bashmak),
    LEVEL_4(4, "Кеды", 14, R.drawable.football, R.drawable.bashmak),
    LEVEL_5(5, "Буцы", 16, R.drawable.football, R.drawable.bashmak),
    RONALDO(6, "Роналдо", 20, R.drawable.football, R.drawable.bashmak),
    MESSI(7, "Месси", 25, R.drawable.football, R.drawable.bashmak),
    ZIDAN(8, "Зидан", 27, R.drawable.football, R.drawable.bashmak),
    END(9, "end", 30, R.drawable.football, R.drawable.bashmak),
    UNKNOWN(-1, "UNKNOWN", -1, R.drawable.football, R.drawable.bashmak);

    final String title;
    final int pushCount;

    final int imageBall;
    final int imageFootwear;
    final int number;

    Level(int number, String title, int pushCount, int imageBall, int imageFootwear) {
        this.number = number;
        this.title = title;
        this.pushCount = pushCount;
        this.imageBall = imageBall;
        this.imageFootwear = imageFootwear;
    }

    public int getImageFootwear() {
        return imageFootwear;
    }

    public int getImageBall() {
        return imageBall;
    }

    public int getPushCount() {
        return pushCount;
    }

    public String getTitle() {
        return title;
    }

    public int getNumber() {
        return number;
    }

    public Level getNextLevel() {
        Level level = values()[ordinal() + 1];
        System.out.println("NEXT LEVEL: " + level.getTitle());
        System.out.println("NEXT LEVEL pushCount: " + level.getPushCount());
        return level;
    }
}
