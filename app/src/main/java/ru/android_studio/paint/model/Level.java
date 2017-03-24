package ru.android_studio.paint.model;

import ru.android_studio.paint.R;

/**
 * Created by yuryandreev on 24/03/2017.
 */

public enum Level {
    SCHOOLBOY("Уровень 1 школьник - пинает хуй", 0, R.drawable.football, R.drawable.bashmak),
    LEVEL_1("LEVEL", 2, R.drawable.football, R.drawable.bashmak),
    LEVEL_2("басота - босоногий мальчуган", 5, R.drawable.football, R.drawable.bashmak),
    LEVEL_3("носки", 7, R.drawable.football, R.drawable.bashmak),
    LEVEL_4("равные кеды", 10, R.drawable.football, R.drawable.bashmak),
    LEVEL_5("кеды", 14, R.drawable.football, R.drawable.bashmak),
    LEVEL_6("буцы", 16, R.drawable.football, R.drawable.bashmak),
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
}
