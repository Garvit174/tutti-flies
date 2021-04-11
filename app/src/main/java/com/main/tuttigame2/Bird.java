package com.main.tuttigame2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.main.tuttigame2.GameView.screenRatioX;
import static com.main.tuttigame2.GameView.screenRatioY;

public class Bird {
    public int speed = 20;
    public boolean wasShot = true;
    int x = 0, y, width, height, birdCounter = 1;
    Bitmap bird1, bird2, bird3, bird4;
    public boolean play_sound_allowed = true;

    Bird (Resources res, int screenFactorX, int screenFactorY) {

        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        width = screenFactorX;
        height = screenFactorY;

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        y = -height;

    }

    Bitmap getBird () {

        if (birdCounter == 1) {
            birdCounter++;
            return bird1;
        }

        if (birdCounter == 2) {
            birdCounter++;
            return bird2;
        }

        if (birdCounter == 3) {
            birdCounter++;
            return bird3;
        }

        birdCounter = 1;

        return bird4;
    }

}
