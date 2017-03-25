package ru.android_studio.paint.service;

import android.content.Context;
import android.media.MediaPlayer;

import ru.android_studio.paint.R;

/**
 * Created by yuryandreev on 25/03/2017.
 */

public class AudioService {

    /**
     * Для воспроизведении песни Газманова при проигрыше
     */
    private MediaPlayer mediaPlayer;


    public void load(Context applicationContext) {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.gazmanchic);
    }

    /**
     * Если нужно остановить музыку Газманова
     */
    protected void onClose() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
