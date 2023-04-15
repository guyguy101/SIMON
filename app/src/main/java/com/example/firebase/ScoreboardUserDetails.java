package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreboardUserDetails extends AppCompatActivity {
    private TextView nameTextView;
    private TextView dateJoinedTextView;
    private TextView lastPlayedTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_user_details);


        Intent intent = getIntent();

        // get the values from the intent
        String name = intent.getStringExtra("name");
        String dateJoined = intent.getStringExtra("date_joined");
        String lastPlayed = intent.getStringExtra("last_played");



        // initialize the TextViews
        nameTextView = findViewById(R.id.user_name_text_view);
        dateJoinedTextView = findViewById(R.id.date_joined);
        lastPlayedTextView = findViewById(R.id.last_date_played);


        // set the values to the TextViews
        nameTextView.setText(name);
        dateJoinedTextView.setText(dateJoined);
        lastPlayedTextView.setText(lastPlayed);
    }
}