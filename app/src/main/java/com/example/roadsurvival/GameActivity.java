package com.example.roadsurvival;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    public static boolean versus;
    public static int carId;
    private GameView gameView;
    private com.example.roadsurvival.GameViewVersus gameViewVersus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // give the point object the size of the screen in the x and y coordinates
        Point point  = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        // give the gameView the size of the screen
        if (versus) {
            gameViewVersus = new com.example.roadsurvival.GameViewVersus(this, point.x, point.y);
            setContentView(gameViewVersus);
        } else {
            gameView = new GameView(this, point.x, point.y, carId);
            setContentView(gameView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause the game
        if (versus) {
            gameViewVersus.pause();
        } else {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume the game
        if (versus) {
            gameViewVersus.resume();
        } else {
            gameView.resume();
        }
    }
}
