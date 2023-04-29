package com.example.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Random;

public class HardSimonGame extends EasySimonGame{

    private int scoreMultiplier = 2;

    public HardSimonGame(ImageButton[] buttons){
        super(buttons);
        DELAY_MILLIS = 250;
        score = 0;
        this.buttons = buttons;

    }
    public HardSimonGame(){
        super();
    }

    public int getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(int scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }
}
