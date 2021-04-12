package com.main.tuttigame2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WineGlass {
    public int speed = 20;
    int x = 0, y, width, height, glass_counter = 1;
    int num_frames_display = 20;
    Bitmap glass1, glass2;
    public boolean play_sound_allowed = true;

    WineGlass (Resources res, int screenFactorX, int screenFactorY) {

        glass1 = BitmapFactory.decodeResource(res, R.drawable.red_wine_glass2_bitmap_main_copy);
        glass2 = BitmapFactory.decodeResource(res, R.drawable.red_wine_glass3_bitmap_main_copy);

        width = screenFactorX;
        height = screenFactorY;

        glass1 = Bitmap.createScaledBitmap(glass1, width, height, false);
        glass2 = Bitmap.createScaledBitmap(glass2, width, height, false);

        y = -height;

    }

    Bitmap get_wine_glass () {

        if (glass_counter >= 1 && glass_counter <= num_frames_display) {
            glass_counter++;
            return glass1;
        }

        if (glass_counter > num_frames_display && glass_counter <= 2*num_frames_display) {
            glass_counter++;
            return glass2;
        }

        glass_counter = 1;

        return glass1;
    }
}
