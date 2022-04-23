package com.example.roadsurvival;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final int[] carSkins = {R.drawable.carblue, R.drawable.carred};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        if (prefs.getString("username", "") == "") {
            startActivity(new Intent(MainActivity.this, Register.class));
        }
        setContentView(R.layout.activity_main);
        int[] skin = {0};
        // making the MainActivity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // listen for leaderboard button
        findViewById(R.id.ranking).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // start the game
                startActivity(new Intent(MainActivity.this, Rankings.class));
            }
        });
        // listen for the play button
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the game
                GameActivity.versus = false;
                GameActivity.carId = carSkins[skin[0]];
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
        // listen for the co-op button
        findViewById(R.id.versus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the game
                GameActivity.versus = true;
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skin[0]++;
                if (skin[0] >= carSkins.length) {
                    skin[0] = 0;
                }
                ImageView image = findViewById(R.id.car);
                image.setImageResource(carSkins[skin[0]]);
            }
        });

        TextView highScoreTxt = findViewById(R.id.highscoretxt);
        TextView name = findViewById(R.id.username);
        String set = "High Score: " + prefs.getInt("highscore", 0);
        String setName = "Name: " + prefs.getString("username", "");
        highScoreTxt.setText(set);
        name.setText(setName);

    }
}