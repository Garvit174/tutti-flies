package com.main.tuttigame2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean is_playing;
    private int score = 0;
    private Paint paint;
    private WineGlass[] wine_glasses;
    private SharedPreferences prefs;
    private Random random;
    private GameActivity activity;
    private Background background1, background2;
    private int targetFPS = 30;
    private double averageFPS;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int background_speed;
    public float screenFactorX, screenFactorY;
    private SoundPool soundPool;
    private int sound0, sound1, sound2, sound3, sound4, sound5, sound6, sound7;
    private int sound_tutti_eating_knaagstok, sound_tutti_eating_tosti;
    private int sound_tutti_eating_pathe;
    private int num_sounds_eating = 3;
    private int min, max;
    private int min_eat, max_eat;
    public float loc_x;
    public float loc_y;
    public int action;
    private Bitmap image;
    private Bitmap cape_image;
    private Bitmap beggin_strip_image;
    private Image tutti_image;
    private Bitmap tutti_body;
    private Bitmap image_hit;
    private int x, y;
    private int x_o, y_o;
    private int xVelocity = 0;
    private int yVelocity = 0;
    private int maxSpeed = 500;
    private CheesyBites[] cheesy_bites;
    private BegginStrip beggin_strip;
    private int num_birds = 4;
    private int num_cheesy_bites = 4;
    private int num_wine_glasses = 2; //Initial number of wine glasses - 2
    private int num_wine_glass_increase = 2;
    private int max_num_wine_glasses = 20;
    private int difficulty_level = 0;
    private int score_interval_for_diff_level = 50;
    private int points_cheesy_bites = 1;
    private int points_beggin_strip = 5;
    private int points_when_beggin_strips_appear = 50;
    private boolean hit_wine_glass = false;

    public GameView(GameActivity activity) {
        super(activity);

        loc_x = 100;
        loc_y = 100;
        x = 100;
        y = 100;
        x_o = 100;
        y_o = 100;
        action = 3;

        min = 0;
        max = 7;

        min_eat = 0;
        max_eat = num_sounds_eating - 1;

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(max+1+num_sounds_eating)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(max + 1 + num_sounds_eating, AudioManager.STREAM_MUSIC, 0);
        }

        sound0 = soundPool.load(activity, R.raw.tutti_0, 1);
        sound1 = soundPool.load(activity, R.raw.tutti_1, 1);
        sound2 = soundPool.load(activity, R.raw.tutti_2, 1);
        sound3 = soundPool.load(activity, R.raw.tutti_3, 1);
        sound4 = soundPool.load(activity, R.raw.tutti_4, 1);
        sound5 = soundPool.load(activity, R.raw.tutti_5, 1);
        sound6 = soundPool.load(activity, R.raw.tutti_6, 1);
        sound7 = soundPool.load(activity, R.raw.tutti_7, 1);
        sound_tutti_eating_knaagstok = soundPool.load(activity, R.raw.tutti_eating_knaagstok, 1);
        sound_tutti_eating_tosti = soundPool.load(activity, R.raw.tutti_eating_tosti, 1);
        sound_tutti_eating_pathe = soundPool.load(activity, R.raw.tuttu_eating_pathe, 1);

        screenFactorX = screenWidth/10;
        screenFactorY = screenHeight/5;
        background_speed = (int) (((float) screenWidth)/200);

        background1 = new Background(screenWidth, screenHeight, getResources());
        background2 = new Background(screenWidth, screenHeight, getResources());
        background2.x = background1.x + background1.background.getWidth();

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.BLACK);

        wine_glasses = new WineGlass[max_num_wine_glasses];
        for (int i = 0;i < max_num_wine_glasses;i++) {

            WineGlass wine_glass = new WineGlass(getResources(), (int) screenFactorX, (int) screenFactorY);
            wine_glasses[i] = wine_glass;

        }

        cheesy_bites = new CheesyBites[num_cheesy_bites];

        random = new Random();

        for (int i = 0;i < num_cheesy_bites;i++) {

            CheesyBites cheesy_bite = new CheesyBites(getResources(), (int) screenFactorX, (int) screenFactorY);
            cheesy_bite.x = random.nextInt(screenWidth - cheesy_bite.getcheesy_bite().getWidth()/2);
            cheesy_bite.y = random.nextInt(screenHeight - cheesy_bite.getcheesy_bite().getHeight()/2);
            cheesy_bites[i] = cheesy_bite;

        }

        beggin_strip = new BegginStrip(getResources(), (int) screenFactorX, (int) screenFactorY);
        beggin_strip_image = beggin_strip.get_beggin_strip();

        tutti_image = new Image(getResources(), (int) screenFactorX, (int) screenFactorY);

        image = tutti_image.get_tutti_image();
        cape_image = tutti_image.get_tutti_cape();

        image_hit = BitmapFactory.decodeResource(getResources(),R.drawable.tutti_hit_bitmap_main_copy);
        image_hit = Bitmap.createScaledBitmap(image_hit, (int) screenFactorX, (int) screenFactorY, false);

        tutti_body = BitmapFactory.decodeResource(getResources(), R.drawable.tutti_flying_body_resized);
        tutti_body = Bitmap.createScaledBitmap(tutti_body, (int) ((screenFactorX*9)/4), (int) ((screenFactorY*8)/4), false);
    }

    public void play_sound_eat() {
        boolean is_mute = prefs.getBoolean("is_mute", false);
        Random rand = new Random();
        int randomNum = rand.nextInt((max_eat - min_eat) + 1) + min_eat;
        if(!is_mute) {
            switch (randomNum) {
                case 0:
                    soundPool.play(sound_tutti_eating_knaagstok, 1, 1, 0, 0, 1);
                    break;
                case 1:
                    soundPool.play(sound_tutti_eating_tosti, 1, 1, 0, 0, 1);
                    break;
                case 2:
                    soundPool.play(sound_tutti_eating_pathe, (float) 0.7, (float) 0.7, 0, 0, 1);
                    break;
            }
        }
    }

    public void play_sound_bark_hit() {
        boolean is_mute = prefs.getBoolean("is_mute", false);
        if(!is_mute) {
            soundPool.play(sound0, 1, 1, 0, 0, 1);
        }
    }

    public void play_sound() {
        boolean is_mute = prefs.getBoolean("is_mute", false);
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        if(!is_mute) {
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
    }


    @Override
    public void run() {

        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/targetFPS;

        while (is_playing) {

            startTime = System.nanoTime();

            update ();
            draw ();

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime-timeMillis;

            try{
                thread.sleep(waitTime);
            }catch(Exception e){}

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if(frameCount == targetFPS)
            {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount =0;
                totalTime = 0;
                System.out.println(averageFPS);
            }

        }

    }

    public int[] update_sprite(float loc_x, float loc_y, int action) {

        x_o = x;
        y_o = y;
        image = tutti_image.get_tutti_image();
        cape_image = tutti_image.get_tutti_cape();
        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            x = (int) loc_x - image.getWidth()/2;
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
            y = (int) loc_y - image.getHeight()/2;
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

        //return x and y
        int[] coordinates = new int[2];
        coordinates[0] = x;
        coordinates[1] = y;

        return coordinates;

    }

    private void update () {

        int[] coordinates_char_sprite;

        coordinates_char_sprite = update_sprite(loc_x, loc_y, action);

        int tutti_x = coordinates_char_sprite[0];
        int tutti_y = coordinates_char_sprite[1];

        x = tutti_x;
        y = tutti_y;

        if(background1.x <= 0) {
            background1.x -= background_speed;
            background2.x = background1.x + background1.background.getWidth() - 20;
        }

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = background1.x + background1.background.getWidth();
            background2.x = 0;
        }

        if (background2.x <= 0) {
            background2.x -= background_speed;
            background1.x = background2.x + background2.background.getWidth() - 20;
        }

        if(score >= points_when_beggin_strips_appear) {
            beggin_strip.speed = background_speed;
            beggin_strip.x -= beggin_strip.speed;
            if (beggin_strip.x + beggin_strip.width < 0) {

                Random rand = new Random();
                int randomNum = rand.nextInt(2 * screenWidth + 1) + screenWidth;

                beggin_strip.x = randomNum;
                beggin_strip.y = random.nextInt(screenHeight - beggin_strip.height);

            }

            float beggin_strip_width = (float) beggin_strip.get_beggin_strip().getWidth();
            float beggin_strip_height = (float) beggin_strip.get_beggin_strip().getHeight();

            float tutti_width = (float) image.getWidth();
            float tutti_height = (float) image.getHeight();

            float del_x = tutti_x - beggin_strip.x;
            float del_y = tutti_y - beggin_strip.y;

            float distance_square = del_x*del_x + del_y*del_y;
            float min_distance_beggin_strip;
            float min_distance_tutti;

            if(beggin_strip_width < beggin_strip_height) {
                min_distance_beggin_strip = beggin_strip_height;
            }
            else {
                min_distance_beggin_strip = beggin_strip_width;
            }

            if(tutti_width < tutti_height) {
                min_distance_tutti = tutti_height;
            }
            else {
                min_distance_tutti = tutti_width;
            }

            float image_distance_square = (min_distance_beggin_strip + min_distance_tutti)*(min_distance_beggin_strip + min_distance_tutti)/2 / 3; // added /3 to minimize image_distance_square

            if(distance_square < image_distance_square) {
                score = score + points_beggin_strip;
                if(beggin_strip.play_sound_allowed) {
                    play_sound_eat();
                    beggin_strip.play_sound_allowed = false;
                }
                beggin_strip.x = -500;
            }
            else {
                beggin_strip.play_sound_allowed = true;
            }
        }

        if((score >= (difficulty_level*score_interval_for_diff_level)) &&
                (score <= ((difficulty_level + 1)*score_interval_for_diff_level)) &&
                (num_wine_glasses <= max_num_wine_glasses)) {
            num_wine_glasses = num_wine_glasses + num_wine_glass_increase;
            difficulty_level++;
        }

        for (int i = 0;i < num_wine_glasses;i++) {

            WineGlass wine_glass = wine_glasses[i];
            wine_glass.x -= wine_glass.speed;

            if (wine_glass.x + wine_glass.width < 0) {

                int bound = 3 * background_speed;
                wine_glass.speed = random.nextInt(bound);

                if (wine_glass.speed < background_speed)
                    wine_glass.speed = background_speed;

                wine_glass.x = screenWidth;
                wine_glass.y = random.nextInt(screenHeight - wine_glass.height);

            }

            float wine_glass_width = (float) wine_glass.get_wine_glass().getWidth();
            float wine_glass_height = (float) wine_glass.get_wine_glass().getHeight();

            float tutti_width = (float) image.getWidth();
            float tutti_height = (float) image.getHeight();

            float del_x = tutti_x - wine_glass.x;
            float del_y = tutti_y - wine_glass.y;

            float distance_square = del_x*del_x + del_y*del_y;
            float min_distance_wine_glass;
            float min_distance_tutti;

            if(wine_glass_width < wine_glass_height) {
                min_distance_wine_glass = wine_glass_height;
            }
            else {
                min_distance_wine_glass = wine_glass_width;
            }

            if(tutti_width < tutti_height) {
                min_distance_tutti = tutti_height;
            }
            else {
                min_distance_tutti = tutti_width;
            }

            float image_distance_square = (min_distance_wine_glass + min_distance_tutti)*(min_distance_wine_glass + min_distance_tutti)/2 / 3; // added /3 to minimize image_distance_square

            if(distance_square < image_distance_square) {
                hit_wine_glass = true;
                if(wine_glass.play_sound_allowed) {
                    play_sound_bark_hit();
                    wine_glass.play_sound_allowed = false;
                }
            }
            else {
                wine_glass.play_sound_allowed = true;
            }

        }

        for (CheesyBites cheesy_bite : cheesy_bites) {

            cheesy_bite.speed = background_speed;
            cheesy_bite.x -= cheesy_bite.speed;

            if (cheesy_bite.x + cheesy_bite.width < 0) {

                Random rand = new Random();
                int randomNum = rand.nextInt(screenWidth + 1) + screenWidth;

                cheesy_bite.x = randomNum;
                cheesy_bite.y = random.nextInt(screenHeight - cheesy_bite.height);

            }

            float cheesy_bite_width = (float) cheesy_bite.getcheesy_bite().getWidth();
            float cheesy_bite_height = (float) cheesy_bite.getcheesy_bite().getHeight();

            float tutti_width = (float) image.getWidth();
            float tutti_height = (float) image.getHeight();

            float del_x = tutti_x - cheesy_bite.x;
            float del_y = tutti_y - cheesy_bite.y;

            float distance_square = del_x*del_x + del_y*del_y;
            float min_distance_cheesy_bite;
            float min_distance_tutti;

            if(cheesy_bite_width < cheesy_bite_height) {
                min_distance_cheesy_bite = cheesy_bite_height;
            }
            else {
                min_distance_cheesy_bite = cheesy_bite_width;
            }

            if(tutti_width < tutti_height) {
                min_distance_tutti = tutti_height;
            }
            else {
                min_distance_tutti = tutti_width;
            }

            float image_distance_square = (min_distance_cheesy_bite + min_distance_tutti)*(min_distance_cheesy_bite + min_distance_tutti)/2 / 4; // added /3 to minimize image_distance_square

            if(distance_square < image_distance_square) {
                score = score + points_cheesy_bites;
                if(cheesy_bite.play_sound_allowed) {
                    play_sound_eat();
                    cheesy_bite.play_sound_allowed = false;
                }
                cheesy_bite.x = -500;
            }
            else {
                cheesy_bite.play_sound_allowed = true;
            }

        }

    }

    private void draw () {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (CheesyBites cheesy_bite : cheesy_bites)
                canvas.drawBitmap(cheesy_bite.getcheesy_bite(), cheesy_bite.x, cheesy_bite.y, null);

            canvas.drawBitmap(beggin_strip_image, beggin_strip.x, beggin_strip.y, null);

            for (int i = 0;i < num_wine_glasses;i++) {
                WineGlass wine_glass = wine_glasses[i];
                canvas.drawBitmap(wine_glass.get_wine_glass(), wine_glass.x, wine_glass.y, null);
            }

            int yh = y + (1*image.getHeight())/3;
            int y2 = yh - cape_image.getHeight()/2;

            int yh_body = y + (5*image.getHeight())/5;
            int y2_body = yh_body - tutti_body.getHeight()/2;

            canvas.drawText(" " + score, 30, screenHeight/6, paint);
            canvas.drawBitmap(cape_image, x - (cape_image.getWidth() - cape_image.getWidth()/3), y2, null);
            canvas.drawBitmap(tutti_body, x - (tutti_body.getWidth() - (tutti_body.getWidth()*3)/5), y2_body, null);
            canvas.drawBitmap(image, x, y, null);

            if(hit_wine_glass) {
                is_playing = false;
                canvas.drawBitmap(image_hit, x, y, null);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            getHolder().unlockCanvasAndPost(canvas);

        }

    }

    private void waitBeforeExiting() {

        try {
            Thread.sleep(2000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void saveIfHighScore() {

        if (prefs.getInt("high_score", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("high_score", score);
            editor.apply();
        }

    }

    public void resume () {

        is_playing = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
            is_playing = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.loc_x = (int) event.getX();
                this.loc_y = (int) event.getY();
                this.action = MotionEvent.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_DOWN:
                this.loc_x = (int) event.getX();
                this.loc_y = (int) event.getY();
                this.action = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_UP:
                this.action = MotionEvent.ACTION_UP;
                break;
        }

        return true;

    }

}
