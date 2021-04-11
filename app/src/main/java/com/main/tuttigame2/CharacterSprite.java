package com.main.tuttigame2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class CharacterSprite {


    private Bitmap image;
    private int x, y;
    private int x_o, y_o;
    private int xVelocity = 10;
    private int yVelocity = 5;
    private int maxSpeed = 500;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private SoundPool soundPool;
    private int sound0, sound1, sound2, sound3, sound4, sound5, sound6, sound7;
    private int min, max;
    private Context context;

    public CharacterSprite (Bitmap bmp, Context context_main) {
        image = bmp;
        x = 100;
        y = 100;
        x_o = 100;
        y_o = 100;
        min = 0;
        max = 7;

        context = context_main;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(max+1)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(max + 1, AudioManager.STREAM_MUSIC, 0);
        }

        sound0 = soundPool.load(context, R.raw.tutti_0, 1);
        sound1 = soundPool.load(context, R.raw.tutti_1, 1);
        sound2 = soundPool.load(context, R.raw.tutti_2, 1);
        sound3 = soundPool.load(context, R.raw.tutti_3, 1);
        sound4 = soundPool.load(context, R.raw.tutti_4, 1);
        sound5 = soundPool.load(context, R.raw.tutti_5, 1);
        sound6 = soundPool.load(context, R.raw.tutti_6, 1);
        sound7 = soundPool.load(context, R.raw.tutti_7, 1);

    }

    public void draw(Canvas canvas) {
       canvas.drawBitmap(image, x, y, null);

    }

    public void play_sound() {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        switch (randomNum) {
            case 0:
                soundPool.play(sound0, 1, 1, 0, 0, 1);
                break;
            case 1:
                soundPool.play(sound1, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(sound2, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(sound3, 1, 1, 0, 0, 1);
                break;
            case 4:
                soundPool.play(sound4, 1, 1, 0, 0, 1);
                break;
            case 5:
                soundPool.play(sound5, 1, 1, 0, 0, 1);
                break;
            case 6:
                soundPool.play(sound6, 1, 1, 0, 0, 1);
                break;
            case 7:
                soundPool.play(sound7, 1, 1, 0, 0, 1);
                break;
        }
    }

    public void update(float loc_x, float loc_y, int action) {

        x_o = x;
        y_o = y;
        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            x = (int) loc_x;
            xVelocity = x - x_o;
        }
        else {
            x += xVelocity;
            if ((x > screenWidth - image.getWidth()) || (x < 0)) {
                xVelocity = xVelocity * -1;
                play_sound();
            }
            if(x > screenWidth - image.getWidth()) {
                x = screenWidth - image.getWidth();
            }
            if(x < 0) {
                x = 0;
            }

        }

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            y = (int) loc_y;
            yVelocity = y - y_o;
        }
        else {
            y += yVelocity;
            if ((y > screenHeight - image.getHeight()) || (y < 0)) {
                yVelocity = yVelocity * -1;
                play_sound();
            }
            if(y > screenHeight - image.getHeight()) {
                y = screenHeight - image.getHeight();
            }
            if(y < 0) {
                y = 0;
            }
        }

        if(yVelocity*yVelocity + xVelocity*xVelocity > maxSpeed*maxSpeed) {
            int xVelocityMax = maxSpeed / (int) sqrt(1 + (yVelocity/xVelocity)*(yVelocity/xVelocity));
            int yVelocityMax = (int) abs(yVelocity / xVelocity) * xVelocityMax;
            if(xVelocity < 0) {
                xVelocity = -xVelocityMax;
            }
            else {
                xVelocity = xVelocityMax;
            }
            if(yVelocity < 0) {
                yVelocity = -yVelocityMax;
            }
            else {
                yVelocity = yVelocityMax;
            }
        }

    }


}


