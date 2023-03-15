package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mBtnRed, mBtnBlue, mBtnGreen, mBtnYellow;
    private ImageButton[] mButtons;
    private Button mBtnStart;
    private ArrayList<Integer> mSequence;
    private int mSequenceIndex;
    private Random mRandom;
    private Handler mHandler;
    private static final int DELAY_MILLIS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);

        mBtnRed = findViewById(R.id.btnRed);
        mBtnBlue = findViewById(R.id.btnBlue);
        mBtnGreen = findViewById(R.id.btnGreen);
        mBtnYellow = findViewById(R.id.btnYellow);
        mButtons = new ImageButton[]{mBtnRed, mBtnBlue, mBtnGreen, mBtnYellow};
        mBtnStart = findViewById(R.id.btnStart);

        mBtnStart.setOnClickListener(( this));
        for (ImageButton button : mButtons) {
            button.setOnClickListener(( this));
        }

        mSequence = new ArrayList<>();
        mRandom = new Random();
        mHandler = new Handler();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnStart) {
            startGame();
        } else {
            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            mHandler.postDelayed(() -> unglowButton((ImageButton) view), DELAY_MILLIS);
        }
    }

    private void startGame() {
        mSequence.clear();
        addToSequence();
        playSequence();
    }

    private void addToSequence() {
        mSequence.add(mRandom.nextInt(mButtons.length));
        mSequenceIndex = 0;
    }

    private void playSequence() {
        for (int i = 0; i < mSequence.size(); i++) {
            int buttonIndex = mSequence.get(i);
            ImageButton button = mButtons[buttonIndex];
            mHandler.postDelayed(() -> glowButton(button), i * DELAY_MILLIS);
            mHandler.postDelayed(() -> unglowButton(button), (i + 1) * DELAY_MILLIS);
        }
    }

    private void glowButton(ImageButton button) {
        button.setAlpha(0.5f);
    }

    private void unglowButton(ImageButton button) {
        button.setAlpha(1.0f);
    }

    private void handleButtonClick(ImageButton button) {
        if (button.getId() == mButtons[mSequence.get(mSequenceIndex)].getId()) {
            mSequenceIndex++;
            if (mSequenceIndex >= mSequence.size()) {
                addToSequence();
                playSequence();
            }
        } else {
            Toast.makeText(this, "Wrong button pressed!", Toast.LENGTH_SHORT).show();
            startGame();
        }
    }
}



