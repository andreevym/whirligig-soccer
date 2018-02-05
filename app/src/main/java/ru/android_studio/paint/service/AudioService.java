package ru.android_studio.paint.service;

import android.content.Context;
import android.media.MediaPlayer;

import ru.android_studio.paint.R;

/**
 * Created by yuryandreev on 25/03/2017.
 */

public class AudioService {

    /**
     * Газманов -
     */
    private MediaPlayer failedMediaPlayer;
    /**
     * Queen - We Are The Champions
     */
    private MediaPlayer winMediaPlayer;


    public void load(Context applicationContext) {
        failedMediaPlayer = MediaPlayer.create(applicationContext, R.raw.gazmanchic);
        winMediaPlayer = MediaPlayer.create(applicationContext, R.raw.quennwearethechampions);
    }

    /**
     * Если нужно остановить музыку
     */
    protected void onClose() {
        if(failedMediaPlayer != null) {
            failedMediaPlayer.stop();
            failedMediaPlayer.release();
        }

        if(winMediaPlayer != null) {
            winMediaPlayer.stop();
            winMediaPlayer.release();
        }
    }

    /**
     * Музыка поражения
     */
    public void failed() {
        if (failedMediaPlayer != null) {
            failedMediaPlayer.start();
        }
    }

    /**
     * Музыка победы
     */
    public void win() {
        if (winMediaPlayer != null) {
            winMediaPlayer.start();
        }
    }
}
