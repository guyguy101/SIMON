package com.example.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Random;

public class EasySimonGame  {

    protected ImageButton[] buttons;
    protected ArrayList<Integer> sequence = new ArrayList<>();
    protected int sequenceIndex;
    protected int score;
    protected int DELAY_MILLIS;

    public EasySimonGame(ImageButton[] buttons){
        DELAY_MILLIS = 1000;
        score = 0;
        this.buttons = buttons;

    }
    public EasySimonGame(){
        DELAY_MILLIS =1000;
        score =0;
    }


    public ArrayList<Integer> getSequence() {
        return sequence;
    }

    public ImageButton[] getButtons() {
        return buttons;
    }

    public int getDELAY_MILLIS() {
        return DELAY_MILLIS;
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setSequence(ArrayList<Integer> sequence) {
        this.sequence = sequence;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

    public void addToSequence(int num){
        sequence.add(num);
    }


}
