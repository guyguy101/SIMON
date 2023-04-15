package com.example.firebase;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class AdvancedSimonGame extends PlayActivity{


    private static final double SCORE_MULTIPLIER =1.2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);
        DELAY_MILLIS= 500;

    }

    @Override
    protected void handleButtonClick(ImageButton button) {
        if (sequence.size() > 0 && button.getId() == buttons[sequence.get(sequenceIndex)].getId()) {
            sequenceIndex++;
            if (sequenceIndex >= sequence.size()) {
                addToSequence();
                playSequence();
                score += score*SCORE_MULTIPLIER;
                etScore.setText(String.valueOf(score));
            }

        } else {
            if (firebaseUser != null) {
                updateUserScore();
            }
            createGameOverDialog();
        }
    }

}
