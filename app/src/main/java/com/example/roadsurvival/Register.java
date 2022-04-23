package com.example.roadsurvival;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText username;
    private TextView register;
    private SharedPreferences prefs;
    private FirebaseFirestore fStore;
    private DocumentReference docRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        username = findViewById(R.id.username);
        register = findViewById(R.id.register);
        prefs = this.getSharedPreferences("game", Context.MODE_PRIVATE);
        fStore = FirebaseFirestore.getInstance();
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String user_txt = username.getText().toString();
                if (TextUtils.isEmpty((user_txt))) {
                    Toast.makeText(Register.this, "Please Enter a username", Toast.LENGTH_SHORT).show();
                } else if (user_txt.length() > 8) {
                    Toast.makeText(Register.this, "Username too long", Toast.LENGTH_SHORT).show();
                } else {
                    docRef = fStore.collection("usernames").document(user_txt);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (!doc.exists()) {
                                    Map<String, Integer> data = new HashMap<>();
                                    data.put("High Score", 0);
                                    docRef.set(data);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("username", user_txt);
                                    editor.apply();
                                    Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, com.example.roadsurvival.MainActivity.class));
                                } else {
                                    Toast.makeText(Register.this, "Username taken", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Register.this, "Failed with: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}