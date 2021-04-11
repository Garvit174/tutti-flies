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
    private boolean isPlaying;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Bird[] birds;
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
    private int min, max;
    public float loc_x;
    public float loc_y;
    public int action;
    private Bitmap image;
    private int x, y;
    private int x_o, y_o;
    private int xVelocity = 0;
    private int yVelocity = 0;
    private int maxSpeed = 500;
    private CheesyBites[] cheesy_bites;
    private int num_birds = 4;
    private int num_cheesy_bites = 4;
    private boolean hit_bird = false;

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

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);


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

        sound0 = soundPool.load(activity, R.raw.tutti_0, 1);
        sound1 = soundPool.load(activity, R.raw.tutti_1, 1);
        sound2 = soundPool.load(activity, R.raw.tutti_2, 1);
        sound3 = soundPool.load(activity, R.raw.tutti_3, 1);
        sound4 = soundPool.load(activity, R.raw.tutti_4, 1);
        sound5 = soundPool.load(activity, R.raw.tutti_5, 1);
        sound6 = soundPool.load(activity, R.raw.tutti_6, 1);
        sound7 = soundPool.load(activity, R.raw.tutti_7, 1);

        this.screenX = screenWidth;
        this.screenY = screenHeight;
        screenRatioX = ((float) screenWidth)/1920f;
        screenRatioY = ((float)screenHeight)/1080f;
        screenFactorX = screenWidth/10;
        screenFactorY = screenHeight/5;
        background_speed = (int) (10 * screenRatioX);

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
        background2.x = background1.x + background1.background.getWidth();

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        birds = new Bird[num_birds];

        for (int i = 0;i < num_birds;i++) {

            Bird bird = new Bird(getResources(), (int) screenFactorX, (int) screenFactorY);
            birds[i] = bird;

        }

        cheesy_bites = new CheesyBites[num_cheesy_bites];

        random = new Random();

        for (int i = 0;i < num_cheesy_bites;i++) {

            CheesyBites cheesy_bite = new CheesyBites(getResources(), (int) screenFactorX, (int) screenFactorY);
            cheesy_bite.x = random.nextInt(screenX - cheesy_bite.getcheesy_bite().getWidth()/2);
            cheesy_bite.y = random.nextInt(screenY - cheesy_bite.getcheesy_bite().getHeight()/2);
            cheesy_bites[i] = cheesy_bite;

        }

        image = BitmapFactory.decodeResource(getResources(),R.drawable.tutti_copy_resized_2);
        image = Bitmap.createScaledBitmap(image, (int) screenFactorX, (int) screenFactorY, false);

    }

    public void play_sound() {
        boolean is_mute = prefs.getBoolean("isMute", false);
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

        while (isPlaying) {

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

        for (Bird bird : birds) {

            bird.x -= bird.speed;

            if (bird.x + bird.width < 0) {

                int bound = 3 * background_speed;
                bird.speed = random.nextInt(bound);

                if (bird.speed < background_speed)
                    bird.speed = background_speed;

                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);

            }

            float bird_width = (float) bird.getBird().getWidth();
            float bird_height = (float) bird.getBird().getHeight();

            float tutti_width = (float) image.getWidth();
            float tutti_height = (float) image.getHeight();

            float del_x = tutti_x - bird.x;
            float del_y = tutti_y - bird.y;

            float distance_square = del_x*del_x + del_y*del_y;
            float min_distance_bird;
            float min_distance_tutti;

            if(bird_width < bird_height) {
                min_distance_bird = bird_height;
            }
            else {
                min_distance_bird = bird_width;
            }

            if(tutti_width < tutti_height) {
                min_distance_tutti = tutti_height;
            }
            else {
                min_distance_tutti = tutti_width;
            }

            float image_distance_square = (min_distance_bird + min_distance_tutti)*(min_distance_bird + min_distance_tutti)/2 / 3; // added /3 to minimize image_distance_square

            if(distance_square < image_distance_square) {
                hit_bird = true;
                if(bird.play_sound_allowed) {
                    play_sound();
                    bird.play_sound_allowed = false;
                }
            }
            else {
                bird.play_sound_allowed = true;
            }

        }

        for (CheesyBites cheesy_bite : cheesy_bites) {

            cheesy_bite.speed = background_speed;
            cheesy_bite.x -= cheesy_bite.speed;

            if (cheesy_bite.x + cheesy_bite.width < 0) {

                cheesy_bite.x = screenX;
                cheesy_bite.y = random.nextInt(screenY - cheesy_bite.height);

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
                score = score + 1;
                if(cheesy_bite.play_sound_allowed) {
                    play_sound();
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
            for (Bird bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, null);

            canvas.drawText("score" + score, 30, screenHeight/6, paint);
            canvas.drawBitmap(image, x, y, null);

            getHolder().unlockCanvasAndPost(canvas);

            if(hit_bird) {
                isPlaying = false;
                saveIfHighScore();
                waitBeforeExiting ();
            }

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

        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    public void resume () {

        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
            isPlaying = false;
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
