package com.example.roadsurvival;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Rankings extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private DocumentReference docRef;
    private CollectionReference username;
    private final int[] userId = {R.id.Name1, R.id.Name2, R.id.Name3, R.id.Name4};
    private final int[] scoreId = {R.id.S1, R.id.S2, R.id.S3, R.id.S4};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fStore = FirebaseFirestore.getInstance();
        username = fStore.collection("usernames");
        Query query = username.orderBy("High Score", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qs = task.getResult();
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        TextView name = findViewById(userId[i]);
                        TextView score = findViewById(scoreId[i]);
                        name.setText(String.valueOf(document.getId()));
                        score.setText(String.valueOf(document.getData().get("High Score")));
                        i += 1;
                        if (i == 4) {
                            break;
                        }
                    }
                    for (int j = i; j < 4; j++) {
                        TextView name = findViewById(userId[j]);
                        TextView score = findViewById(scoreId[j]);
                        name.setText("N/A");
                        score.setText("N/A");
                    }
                }
            }
        });
        setContentView(R.layout.activity_rankings);
        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Rankings.this, com.example.roadsurvival.MainActivity.class));
            }
        });
    }
}
