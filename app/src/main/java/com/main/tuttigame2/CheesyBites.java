package com.main.tuttigame2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CheesyBites {
    public int speed = 20;
    int x = 0, y, width, height;
    Bitmap cheasy_bite;
    public boolean play_sound_allowed = true;

    CheesyBites (Resources res, int screenFactorX, int screenFactorY) {

        cheasy_bite = BitmapFactory.decodeResource(res, R.drawable.cheasy_bite_resized);

        width = screenFactorX;
        height = screenFactorY;

        cheasy_bite = Bitmap.createScaledBitmap(cheasy_bite, width, height, false);

        y = -height;

    }

    public Bitmap getcheesy_bite () {
        return this.cheasy_bite;
    }
}
