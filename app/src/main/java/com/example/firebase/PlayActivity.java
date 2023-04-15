package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

    protected ImageButton[] buttons;
    protected Button btnStart, btnGameOverExit;
    protected ArrayList<Integer> sequence = new ArrayList<>();
    protected int sequenceIndex;
    protected int score = 0;
    protected EditText etScore, etFinalScore;
    protected int DELAY_MILLIS ;
    protected Dialog gameOverDialog;
    protected Intent intent;
    protected FirebaseUser firebaseUser;
    protected FirebaseAuth firebaseAuth;
    protected Handler handler = new Handler();
    protected Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        DELAY_MILLIS = 1000;
        firebaseUser= firebaseAuth.getCurrentUser();
        buttons = new ImageButton[]{
                findViewById(R.id.btnRed),
                findViewById(R.id.btnBlue),
                findViewById(R.id.btnGreen),
                findViewById(R.id.btnYellow)
        };

        etScore = findViewById(R.id.etScore);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        for (ImageButton button : buttons) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGameOverExit) {
            gameOverDialog.dismiss();
            intent = new Intent(this, OpenActivity.class);
            startActivity(intent);
            finish();

        } else if (view.getId() == R.id.btnStart) {
            startGame();
        } else {
            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), DELAY_MILLIS);
        }
    }

    protected void startGame() {
        sequence.clear();
        addToSequence();
        playSequence();
    }

    protected void addToSequence() {
        sequence.add(random.nextInt(buttons.length));
        sequenceIndex = 0;
    }

    protected void playSequence() {
        for (int i = 0; i < sequence.size(); i++) {
            int buttonIndex = sequence.get(i);
            ImageButton button = buttons[buttonIndex];
            handler.postDelayed(() -> {
                glowButton(button);
                handler.postDelayed(() -> unglowButton(button), DELAY_MILLIS / 2);
            }, i*DELAY_MILLIS);
        }
    }

     protected void handleButtonClick(ImageButton button) {
        if (sequence.size() > 0 && button.getId() == buttons[sequence.get(sequenceIndex)].getId()) {
            sequenceIndex++;
            if (sequenceIndex >= sequence.size()) {
                addToSequence();
                playSequence();
                score++;
                etScore.setText(String.valueOf(score));
            }

        } else {
            if (firebaseUser != null) {
                updateUserScore();
            }
            createGameOverDialog();
        }
    }

    private void glowButton(ImageButton button) {
        button.setAlpha(0.5f);
    }

    private void unglowButton(ImageButton button) {
        if (sequence.contains(sequence.indexOf(button))) {
            button.setAlpha(0.5f);
        } else {
            button.setAlpha(1.0f);
        }
    }

    protected void updateUserScore() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference maxScoreRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("maxScore");

        maxScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxScore = dataSnapshot.getValue(Integer.class);
                if (maxScore < score) {
                    maxScore = score;

                    UserDatabase dbHelper = new UserDatabase(PlayActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(UserDatabase.COLUMN_MAX_SCORE, maxScore);
                    String whereClause = UserDatabase.COLUMN_ID + "=?";
                    String[] whereArgs = {userId};
                    db.update(UserDatabase.TABLE_NAME, values, whereClause, whereArgs);
                    db.close();
                }
                maxScoreRef.setValue(maxScore);
                etScore.setText(String.valueOf(score));
                score = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error reading max score from database: " + databaseError.getMessage());
            }
        });
    }
    protected void createGameOverDialog(){

        gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.gameover_layout);
        gameOverDialog.setTitle("Game Over");
        gameOverDialog.setCancelable(false);
        etFinalScore = gameOverDialog.findViewById(R.id.etFinalScore);
        etFinalScore.setText(Integer.toString(score));
        btnGameOverExit = gameOverDialog.findViewById(R.id.btnGameOverExit); // use d.findViewById instead of findViewById
        btnGameOverExit.setOnClickListener(this::onClick);

        gameOverDialog.show();

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



