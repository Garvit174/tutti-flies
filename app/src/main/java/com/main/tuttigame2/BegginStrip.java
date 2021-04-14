package com.main.tuttigame2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BegginStrip {
    public int speed = 20;
    int x = 0, y, width, height;
    Bitmap beggin_strip;
    public boolean play_sound_allowed = true;

    BegginStrip (Resources res, int screenFactorX, int screenFactorY) {

        beggin_strip = BitmapFactory.decodeResource(res, R.drawable.beggin_strip_cropped);

        width = screenFactorX - screenFactorX/3;
        height = screenFactorY - screenFactorY/3;

        beggin_strip = Bitmap.createScaledBitmap(beggin_strip, width, height, false);

        y = -height;

    }

    public Bitmap get_beggin_strip() {
        return this.beggin_strip;
    }
}
