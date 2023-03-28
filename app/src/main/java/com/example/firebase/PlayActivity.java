package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.FillEventHistory;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnRed, btnBlue, btnGreen, btnYellow;
    private ImageButton[] buttons;
    private Button btnStart , btnGameOverExit;
    private ArrayList<Integer> sequence;
    private int sequenceIndex;
    private Random random;
    private Handler handler;
    int score =0;
    EditText etScore ,etFinalScore;
    private static final int DELAY_MILLIS = 1000;
    private Dialog d;
    private Intent intent;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);

        btnRed = findViewById(R.id.btnRed);
        btnBlue = findViewById(R.id.btnBlue);
        btnGreen = findViewById(R.id.btnGreen);
        btnYellow = findViewById(R.id.btnYellow);
        buttons = new ImageButton[]{btnRed, btnBlue, btnGreen, btnYellow};
        btnStart = findViewById(R.id.btnStart);

        etScore = findViewById(R.id.etScore);


        btnStart.setOnClickListener(( this));
        for (ImageButton button : buttons) {
            button.setOnClickListener(( this));
        }

        sequence = new ArrayList<>();
        random = new Random();
        handler = new Handler();
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnGameOverExit)
        {
            intent= new Intent(this,OpenActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.btnStart) {
            startGame();
        } else {
            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), DELAY_MILLIS);
        }

    }
    //region Game Logic
    private void startGame() {
        sequence.clear();
        addToSequence();

        playSequence();
    }

    private void addToSequence() {
        sequence.add(random.nextInt(buttons.length));
        sequenceIndex = 0;


    }

    private void playSequence() {
        for (int i = 0; i < sequence.size(); i++) {
            int buttonIndex = sequence.get(i);
            ImageButton button = buttons[buttonIndex];
            handler.postDelayed(() -> {
                glowButton(button);
                handler.postDelayed(() -> unglowButton(button), DELAY_MILLIS);
            }, i * DELAY_MILLIS);
        }
    }



    private void glowButton(ImageButton button) {
        button.setAlpha(0.5f);
    }

    private void unglowButton(ImageButton button) {
        button.setAlpha(1.0f);
    }

    private void handleButtonClick(ImageButton button) {
        if (sequence.size() >0 &&button.getId() == buttons[sequence.get(sequenceIndex)].getId()) {
            sequenceIndex++;
            if (sequenceIndex >= sequence.size()) {
                addToSequence();
                playSequence();
                score++;
                etScore.setText(Integer.toString(score));



            }


        }

        else {
            if(firebaseUser != null)
                updateUserScore();

            createGameOverDialog();
        }


    }
    private void updateUserScore(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference maxScoreRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("maxScore");

        maxScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the maxScore value as an integer
                int maxScore = dataSnapshot.getValue(Integer.class);

                if(maxScore < score)
                    maxScore = score;
                maxScoreRef.setValue(maxScore);
                etScore.setText(Integer.toString(score));
                score = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e(TAG, "Error reading max score from database: " + databaseError.getMessage());
            }
        });


    }
    private void createGameOverDialog(){
        d= new Dialog(this);
        d.setContentView(R.layout.gameover_layout);
        d.setTitle("Game Over");
        d.setCancelable(true);
        etFinalScore = d.findViewById(R.id.etFinalScore);
        etFinalScore.setText(Integer.toString(score));
        btnGameOverExit = d.findViewById(R.id.btnGameOverExit); // use d.findViewById instead of findViewById
        btnGameOverExit.setOnClickListener(this::onClick);

        d.show();
    }
//endregion
    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playing_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backItem:
                intent = new Intent(this, OpenActivity.class);
                startActivity(intent);
                finish();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //endregion
}



