package com.example.roadsurvival;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying = true;
    private boolean isGameOver = false;
    private com.example.roadsurvival.GameActivity activity;
    private SharedPreferences prefs;
    private int screenX;
    private int screenY;
    private com.example.roadsurvival.Background background1, background2;
    private com.example.roadsurvival.Obstacle[] obstacles;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private Paint paint;
    private long t0=0;
    private long score;
    private com.example.roadsurvival.Car car;
    private MediaPlayer mediaPlayer;
    private FirebaseFirestore fStore;
    private DocumentReference docRef;
    private final String[] ranks = {"Rank1", "Rank2", "Rank3", "Rank4", "Rank5"};

    public GameView(com.example.roadsurvival.GameActivity activity, int screenX, int screenY, int carId) {
        super(activity);
        isPlaying = true;
        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);
        this.screenX = screenX;
        this.screenY = screenY;
        background1 = new com.example.roadsurvival.Background(screenX, screenY, getResources());
        background2 = new com.example.roadsurvival.Background(screenX, screenY, getResources());

        car = new com.example.roadsurvival.Car(screenX*2/5, screenY*17/24, carId, getResources(), screenX, false);
        background2.y = -screenY;
        background2.y = -screenY;



        // paint object that will draw everything onto the canvas
        paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        // number of obstacles on screen
        obstacles = new com.example.roadsurvival.Obstacle[4];

        // assign each index of the obstacles array to an obstacle
        random = new Random();

        for (int i = 0; i < obstacles.length; i++) {
            // can be edited with a random image
            obstacles[i] = new com.example.roadsurvival.Obstacle(R.drawable.log, getResources(), screenX, screenY);
            obstacles[i].x = random.nextInt(screenX - obstacles[i].width);
            // obstacles start off of the screen and will get to the screen at a random time
            obstacles[i].y = -(random.nextInt(screenY) + obstacles[i].height);
            //obstacles[i].y = 0;
//          System.out.println("OBSTACLE: " + obstacles[i].x + ", " + obstacles[i].y);
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

        // database
        fStore = FirebaseFirestore.getInstance();
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
        System.out.println(screenY);

        // updating every obstacle
        for (com.example.roadsurvival.Obstacle obstacle : obstacles) {

            // for obstacles that move off the screen
            if(obstacle.y > screenY) {
                // make a new obstacle on the screen
                //obstacle.y = -(random.nextInt(50) + obstacle.height);
                obstacle.y = -(random.nextInt(screenY));
                //obstacle.x = random.nextInt(screenY - obstacle.height);
                obstacle.x = random.nextInt(screenX - obstacle.width);
            }

            // update movement of obstacle, moves faster and faster as score increases
            //obstacle.y += (int) (screenY/108 + (score/(screenY/54)));
            obstacle.y += (int) 10 + (score/54);

            // check for collisions between car and obstacles
            if(Rect.intersects(car.getCollisionShape(), obstacle.getCollisionShape())) {
                // stops bgm
                mediaPlayer.stop();
                // play boom sound
                soundPool.play(sound, 100, 100, 1, 0, 1);
                // end the game if the car collides with an obstacle
                isGameOver = true;
                break;
            }

        }
        System.out.println(background1);
        // background moves. It moves faster as score/time increases.
        //background1.y += (int) (screenY/108 + (score/(screenY/54)));
        //background2.y += (int) (screenY/108 + (score/(screenY/54)));
        background1.y += (int) 10 + score/54;
        background2.y += (int) 10 + score/54;

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

            // draw obstacles
            for(com.example.roadsurvival.Obstacle obstacle : obstacles) {
                if(obstacle.y >= 0) {
                    canvas.drawBitmap(obstacle.image, obstacle.x, obstacle.y, paint);
                }
            }
            canvas.drawText(String.valueOf(score),screenX*1/10,screenY*1/10, paint);

            // when the game is over
            if(isGameOver) {
                mediaPlayer.start();
                isPlaying = false;
                boolean high = prefs.getInt("highscore", 0) < score;
                if (high) {
                    String name = prefs.getString("username", "");
                    docRef = fStore.collection("usernames").document(name);
                    Map<String, Integer> newScore = new HashMap<>();
                    newScore.put("High Score", (int) score);
                    docRef.set(newScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("highscore", (int) score);
                            editor.apply();
                        }
                    });
                    //updateRankings((int) score);
                }
                //saveIfHighScore(canvas, paint);
                // [death animation]
                canvas.drawBitmap(background1.background, 0, 0, paint);
                canvas.drawText("Game Over", screenX/2 - 250, (int) (screenY/2), paint);
                paint.setTextSize(75);
                canvas.drawText("Score: " + score, screenX/2 - 175, (int) (screenY/2) + 100, paint);
                if (high) {
                    canvas.drawText("New High Score!!", screenX/2 - 300, (int) (screenY/2) - 100, paint);
                }
                getHolder().unlockCanvasAndPost(canvas);
                waitBeforeExiting();
                return;
            }
            // post canvas on the screen
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /*private void updateRankings(int score) {
        Map<String, String> updateScore = new HashMap<>();
        updateScore.put("score", String.valueOf(score));
        String names = prefs.getString("username", "");
        updateScore.put("username", names);
        final boolean[] stop = {false};
        for (int i = 0; i < 5; i++) {
            docRef = fStore.collection("Leaderboard").document(ranks[0]);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String recordName = documentSnapshot.getString("username");
                        int high = Integer.parseInt(documentSnapshot.getString("score"));
                        if (score >= high) {
                            docRef.set(updateScore);
                            updateScore.clear();
                            if (!names.equals(recordName)) {
                                updateScore.put("username", recordName);
                                updateScore.put("score", String.valueOf(high));
                            } else {
                                stop[0] = true;
                            }

                        }
                    } else {
                        docRef.set(updateScore);
                        stop[0] = true;
                    }
                }
            });
            if (stop[0]) { i = 5; }
        }
    }*/

    private boolean saveIfHighScore(Canvas canvas, Paint paint) {
        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", (int) score);
            editor.apply();
        }
        return prefs.getInt("highscore", 0) < score;
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
            activity.startActivity(new Intent(activity, com.example.roadsurvival.MainActivity.class));
            // Finish the game activity
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // method gets called whenever the screen is touched
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // press down
            case MotionEvent.ACTION_DOWN:
                if (event.getX() >= car.x && event.getX() <= car.x + car.width) {
                    car.setActionDown(true, -1);
                }
                break;
            // finger moved along screen
            case MotionEvent.ACTION_MOVE:
                if(car.getActionDown()) {
                    car.setPosition(event.getX(), car.y, false);
                }
                break;
            // finger released
            case MotionEvent.ACTION_UP:
                car.setActionDown(false, -1);
                break;
        }
        return true;
    }
}