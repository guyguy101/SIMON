package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OpenActivity extends AppCompatActivity implements View.OnClickListener {
Button btnPlay;
Button btnScoreboard;

Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_simon);


        btnPlay= findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnScoreboard=findViewById(R.id.btnScoreboard);
        btnScoreboard.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        if(view==btnPlay){
            intent= new Intent(this,PlayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
