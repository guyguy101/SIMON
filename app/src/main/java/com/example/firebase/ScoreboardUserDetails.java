package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ScoreboardUserDetails extends AppCompatActivity {
    private TextView nameTextView;
    private TextView dateJoinedTextView;
    private TextView lastPlayedTextView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_user_details);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.setVisible(true, true);
        animationDrawable.start();

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

    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);
        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.backItem:
                intent = new Intent(this,ScoreboardActivity.class);
                startActivity(intent);
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }

    }
}