package com.main.tuttigame2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
    int x = 0, y, width, height, image_counter = 1;
    int cape_counter = 1;
    int num_frames_display = 2;
    Bitmap image1, image2;
    Bitmap cape1, cape2;

    Image (Resources res, int screenFactorX, int screenFactorY) {

        image1 = BitmapFactory.decodeResource(res, R.drawable.tutti_main_image_cropped_resized);
        image2 = BitmapFactory.decodeResource(res, R.drawable.tutti_main_image_cropped_resized);

        cape1 = BitmapFactory.decodeResource(res, R.drawable.cape1_cropped_white_removed_copy);
        cape2 = BitmapFactory.decodeResource(res, R.drawable.cape2_cropped_white_removed_copy);

        width = screenFactorX;
        height = screenFactorY;

        image1 = Bitmap.createScaledBitmap(image1, width, height, false);
        image2 = Bitmap.createScaledBitmap(image2, width, height, false);

        cape1 = Bitmap.createScaledBitmap(cape1, (width*3)/2, (height*6)/2, false);
        cape2 = Bitmap.createScaledBitmap(cape2, (width*3)/2, (height*6)/2, false);

        y = -height;

    }

    Bitmap get_tutti_cape () {

        if (cape_counter >= 1 && cape_counter <= num_frames_display) {
            cape_counter++;
            return cape1;
        }

        if (cape_counter > num_frames_display && cape_counter <= 2*num_frames_display) {
            cape_counter++;
            return cape2;
        }

        cape_counter = 1;

        return cape1;
    }

    Bitmap get_tutti_image () {

        if (image_counter >= 1 && image_counter <= num_frames_display) {
            image_counter++;
            return image1;
        }

        if (image_counter > num_frames_display && image_counter <= 2*num_frames_display) {
            image_counter++;
            return image2;
        }

        image_counter = 1;

        return image1;
    }
}
