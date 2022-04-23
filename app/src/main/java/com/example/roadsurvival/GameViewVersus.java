package com.example.roadsurvival;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Random;

public class GameViewVersus extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private GameActivity activity;
    private static int screenX;
    private static int screenY;
    private com.example.roadsurvival.Background background1, background2;
    private Obstacle[] obstacles;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private Paint paint;
    private long t0=0;
    private long score;
    private Car car;
    private Car car2;
    private String winner;
    private String loser;
    private MediaPlayer mediaPlayer;
    private int pointer;
    private int pointerID;
    private int pointerID2;

    public GameViewVersus(GameActivity activity, int screenX, int screenY) {
        super(activity);
        isPlaying = true;
        this.activity = activity;
        this.screenX = screenX;
        this.screenY = screenY;
        background1 = new com.example.roadsurvival.Background(screenX, screenY, getResources());
        background2 = new com.example.roadsurvival.Background(screenX, screenY, getResources());
        car = new Car(screenX*1/5, screenY*17/24, R.drawable.carblue, getResources(), screenX, true);
        car2 = new Car(screenX*3/5, screenY*17/24, R.drawable.carred, getResources(), screenX, false );
        winner = "";
        loser = "";

        background2.y = -screenY;

        // paint object that will draw everything onto the canvas
        paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        // number of obstacles on screen
        obstacles = new Obstacle[4];

        // assign each index of the obstacles array to an obstacle
        random = new Random();

        for (int i = 0; i < obstacles.length; i++) {
            // can be edited with a random image
            obstacles[i] = new Obstacle(R.drawable.log, getResources(), screenX, screenY);
            obstacles[i].x = random.nextInt((screenX - obstacles[i].width)/2);
            // obstacles start off of the screen and will get to the screen at a random time
            obstacles[i].y = -(random.nextInt(screenY) + obstacles[i].height);
            if (i >= obstacles.length/2) {
                obstacles[i].x += screenX/2;
            }
        }
        t0 = System.nanoTime();

        // sets up short sounds to be played, instantiating soundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        // sets the boom sound ready
        sound = soundPool.load(activity, R.raw.boom, 1);
        // instantiates and plays bgm
        mediaPlayer = MediaPlayer.create(activity, R.raw.bgm);
        mediaPlayer.start();

    }



    @Override
    public void run() {
        while(isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        long t1 = System.nanoTime();
        score = (long) ((t1-t0) * 0.00000001);


        // updating every obstacle
        for (Obstacle obstacle : obstacles) {

            // for obstacles that move off the screen
            if(obstacle.y > screenY) {
                // make a new obstacle on the screen
                //obstacle.y = -(random.nextInt(50) + obstacle.height);
                obstacle.y = -(random.nextInt(screenY));
                //obstacle.x = random.nextInt(screenY - obstacle.height);
                if (obstacle.x <= (screenX - obstacle.width)/2) {
                    obstacle.x = 0;
                } else {
                    obstacle.x = (screenX - obstacle.width)/2;
                }
                obstacle.x += random.nextInt((screenX - obstacle.width)/2);
            }

            // update movement of obstacle, moves faster and faster as score increases
            obstacle.y += (int) (screenY/108 + (score/(screenY/54)));

            // check for collisions between car and obstacles
            if(Rect.intersects(car.getCollisionShape(), obstacle.getCollisionShape())) {
                // stops bgm
                mediaPlayer.stop();
                // play boom sound
                soundPool.play(sound, 100, 100, 1, 0, 1);
                // who wins?/loses?
                winner = "Red";
                loser = "Blue";
                // end the game if the car collides with an obstacle
                isGameOver = true;
                break;
            }
            if(Rect.intersects(car2.getCollisionShape(), obstacle.getCollisionShape())) {
                // stops bgm
                mediaPlayer.stop();
                // play boom sound
                soundPool.play(sound, 100, 100, 1, 0, 1);
                // who wins? loses?
                winner = "Blue";
                loser = "Red";
                // end the game if the car collides with an obstacle
                isGameOver = true;
                break;
            }

        }
        // background moves. It moves faster as score/time increases.
        background1.y += (int) (screenY/108 + (score/(screenY/54)));
        background2.y += (int) (screenY/108 + (score/(screenY/54)));

        // if one of the backgrounds moves completely off the screen, put it on the other side of the other background
        if (background1.y > background1.background.getHeight()) {
            background1.y = background2.y - background1.background.getHeight();
        }

        if (background2.y > background2.background.getHeight()) {
            background2.y = background1.y - background2.background.getHeight();
        }

    }

    private void draw() {
        if(getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            // draw objects on the screen

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            canvas.drawBitmap(car.car, car.x, car.y, paint);
            canvas.drawBitmap(car2.car, car2.x, car2.y, paint);

            // draw obstacles
            for(Obstacle obstacle : obstacles) {
                if(obstacle.y >= 0) {
                    canvas.drawBitmap(obstacle.image, obstacle.x, obstacle.y, paint);
                }
            }

            // when the game is over
            if(isGameOver) {
                mediaPlayer.start();
                isPlaying = false;
                // [death animation]
                canvas.drawBitmap(background1.background, 0, 0, paint);
                canvas.drawText(winner + " wins!!", screenX/2 - 250, screenY/2, paint);
                paint.setTextSize(50);
                canvas.drawText("(Better luck next time, " + loser + "!)", screenX/2 - 250, screenY/2 + 100, paint);
                paint.setTextSize(75);
                getHolder().unlockCanvasAndPost(canvas);
                waitBeforeExiting();
                return;
            }
            // post canvas on the screen
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    // thread methods
    private void sleep () {
        try {
            // 1000 ms / 17 ms = 60  using this sleep() will give us 60 fps
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            // stops the thread
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void waitBeforeExiting() {

        try {
            Thread.sleep(5000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            // Finish the game activity
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    // method gets called whenever the screen is touched
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            // press down
            case MotionEvent.ACTION_DOWN:
                if (car.getCollisionShape().contains((int)event.getX(), (int)event.getY())) {
                    pointerID = event.getPointerId(0);
                    car.setActionDown(true, pointerID);
                } else if (car2.getCollisionShape().contains((int)event.getX(), (int)event.getY())) {
                    pointerID2 = event.getPointerId(0);
                    car2.setActionDown(true, pointerID2);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (car.getID() < 0 && car.getCollisionShape().contains((int)event.getX(event.getActionIndex()), (int)event.getY(event.getActionIndex()))) {
                    pointerID = event.getPointerId(1);
                    car.setActionDown(true, pointerID);
                } else if (car.getID() < 0 && car2.getCollisionShape().contains((int)event.getX(event.getActionIndex()), (int)event.getY(event.getActionIndex()))) {
                    pointerID2 = event.getPointerId(1);
                    car2.setActionDown(true, pointerID2);
                }
                break;
            // finger moved along screen
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // moves the cars horizontally where the fingers are moved
                    if(car.getActionDown() && car.getID() == event.getPointerId(event.getActionIndex())) {
                        car.setPosition(event.getX(event.getActionIndex()), car.y, true);
                    }
                    if (car2.getActionDown() && car2.getID() == event.getPointerId(event.getActionIndex())) {
                        car2.setPosition(event.getX(event.getActionIndex()), car2.y, true);
                    }
                } else if (event.getPointerCount() >= 2){
                    car.setPosition(event.getX(event.findPointerIndex(pointerID)), car.y, true);
                    car2.setPosition(event.getX(event.findPointerIndex(pointerID2)), car2.y, true);
                }
                break;
            // finger released
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // conditionals check which car is tapped on
                if (car.getID() == pointerID) {
                    car.setActionDown(false, -1);
                } else if (car2.getID() == pointerID2) {
                    car2.setActionDown(false, -1);
                }
                break;
        }
        return true;
    }
}