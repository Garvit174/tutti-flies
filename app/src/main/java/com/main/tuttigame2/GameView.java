package com.main.tuttigame2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private CharacterSprite characterSprite;
    public float loc_x;
    public float loc_y;
    public int action;
    private Context context;

    public GameView(Context context_main) {
        super(context_main);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(),this);

        loc_x = 100;
        loc_y = 100;
        action = 3;

        context = context_main;
        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        characterSprite = new CharacterSprite(BitmapFactory.decodeResource(getResources(),R.drawable.tutti_copy_resized_2), context);

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

        } catch(InterruptedException e){
            e.printStackTrace();
        }
        retry = false;
    }
    }

    public void update() {
        characterSprite.update(loc_x, loc_y, action);

    }

    @Override
    public void draw(Canvas canvas)
    {

        super.draw(canvas);
        if(canvas!=null) {
            characterSprite.draw(canvas);

        }
    }


}







