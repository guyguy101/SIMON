package com.example.firebase;



import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

    protected ImageButton[] buttons = new ImageButton[4];
    protected Button btnStart, btnGameOverExit;
    protected TextView etScore, etFinalScore;
    protected Dialog gameOverDialog;
    protected Intent intent;
    protected FirebaseUser firebaseUser;
    protected FirebaseAuth firebaseAuth;
    protected Handler handler = new Handler();
    protected Random random = new Random();
    EasySimonGame game;
    protected WifiReceiver wifiReceiver = new WifiReceiver();
    private int buttonsPressed =0;

    private Handler gameHandler = new Handler();
    private Runnable gameOverRunnable = new Runnable() {
        @Override
        public void run() {
            updateUserScore();
            Toast.makeText(PlayActivity.this,"Timed out for not playing", Toast.LENGTH_LONG);
            createGameOverDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);



        firebaseAuth = FirebaseAuth.getInstance();
        
        firebaseUser= firebaseAuth.getCurrentUser();
        buttons[0] =findViewById(R.id.btnRed);
        buttons[1] =findViewById(R.id.btnBlue);
        buttons[2] =findViewById(R.id.btnGreen);
        buttons[3] =findViewById(R.id.btnYellow);
        buttons[0].setOnClickListener(this);
        buttons[1].setOnClickListener(this);
        buttons[2].setOnClickListener(this);
        buttons[3].setOnClickListener(this);
        game = new EasySimonGame(buttons);
        SetGameMode();

        etScore = findViewById(R.id.etScore);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);


    }

    public EasySimonGame SetGameMode(){
        //הקושי רמת לפי משחק אובייקט המחזירה פעולה
        if(OpenActivity.GAME_MODE_CODE == 1)
            return game = new HardSimonGame(buttons);
        return game;
    }



    @Override
    public void onClick(View view) {
        gameHandler.removeCallbacks(gameOverRunnable); // remove any previously scheduled game over runnable
        gameHandler.postDelayed(gameOverRunnable, 10000); // schedule the game over runnable to run in 10 seconds
        if (view.getId() == R.id.btnGameOverExit) {
            gameOverDialog.dismiss();
            intent = new Intent(this, OpenActivity.class);
            startActivity(intent);
            finish();

        } else if (view.getId() == R.id.btnStart) {
            startGame();
            handler.postDelayed(() -> {
                boolean buttonPressed = false;
                for (ImageButton button : buttons) {
                    if (button.isPressed()) {
                        buttonPressed = true;
                        break;
                    }
                }
                if (!buttonPressed) {
                    Toast.makeText(PlayActivity.this,"Timed out for not playing", Toast.LENGTH_LONG);
                    createGameOverDialog();

                }
            }, 10000);

        } else if(view.getId() == R.id.btnRed){
            //הקושי רמת לפי דיליי כמות עם הכפתור את להאיר
            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), game.DELAY_MILLIS);
        }
        else if(view.getId() == R.id.btnGreen){

            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), game.DELAY_MILLIS);
        }
        else if(view.getId() == R.id.btnYellow){

            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), game.DELAY_MILLIS);
        }
        else if(view.getId() == R.id.btnBlue){

            handleButtonClick((ImageButton) view);
            glowButton((ImageButton) view);
            handler.postDelayed(() -> unglowButton((ImageButton) view), game.DELAY_MILLIS);
        }

    }

    //region BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }
    //endregion

    protected void startGame() {
        //פעולה המתחילה את המשחק
        game.setSequence(new ArrayList<Integer>());
        addToSequence();
        playSequence();
    }

    protected void addToSequence() {
        //פעולה המוסיפה מספר לרצף בין אפס לארבע שמייצגים את האינדקס של כל כפתור
        //משתנה הרביעי הכפתור רק אז ארבע לגודל מגיע הרצף כאשר אז קל במצב המשחק אם
        //לגדול ממשיך הרצף אחרת
        if(OpenActivity.GAME_MODE_CODE == 1){
            game.addToSequence(random.nextInt(buttons.length));
        }
        else{
            if(game.getSequence().size() < 4)
                game.addToSequence(random.nextInt(buttons.length));
            else if(game.getSequence().size() == 4){
                ArrayList<Integer> updatedSequence = new ArrayList<>();
                updatedSequence = game.getSequence();
                updatedSequence.set(3,random.nextInt(buttons.length));
                game.setSequence(updatedSequence);
            }
        }
        game.setSequenceIndex(0);
    }

    protected void playSequence() {
        //עליהם יחזור שהמשתמש כדי ברצף שנמצאים הכפתורים את להאיר
        for (int i = 0; i < game.sequence.size(); i++) {
            int buttonIndex = game.getSequence().get(i);

            ImageButton button = buttons[buttonIndex];

            handler.postDelayed(() -> {
                glowButton(button);
                handler.postDelayed(() -> unglowButton(button), game.DELAY_MILLIS / 2);
            }, i*game.DELAY_MILLIS);
        }
    }

     protected void handleButtonClick(ImageButton button) {
         //כפתור על לחיצה עם המתמודדת פעולה
         //קל ממשחק יותר בשתיים תעלה התוצאה אז קשה מסוג המשחק אם
         //המשתמש נתוני את יעדכן רשום המשתמש אם
         //נפסלת אם משחק סיום דיאלוג יוצרת
         ++buttonsPressed;
        if (game.getSequence().size() > 0 && button.getId() == buttons[game.getSequence().get(game.getSequenceIndex())].getId()) {
            game.setSequenceIndex(++game.sequenceIndex);
            if (game.getSequenceIndex() >= game.getSequence().size()) {
                addToSequence();
                playSequence();
                if(game instanceof HardSimonGame){
                    int updatedScore = game.getScore() + 1 + ((HardSimonGame) game).getScoreMultiplier();
                    game.setScore(updatedScore);
                    etScore.setText(String.valueOf(game.getScore()));
                }
                else{
                    int updatedScore = game.getScore() +1;
                    game.setScore(updatedScore);
                    etScore.setText(String.valueOf(game.getScore()));
                }

            }

        } else {
            if (firebaseUser != null) {
                updateUserScore();
            }
            createGameOverDialog();
        }
    }

    protected void glowButton(ImageButton button) {
        button.setAlpha(0.5f);
    }

    protected void unglowButton(ImageButton button) {
        if (game.sequence.contains(game.getSequence().indexOf(button))) {
            button.setAlpha(0.5f);
        } else {
            button.setAlpha(1.0f);
        }
    }

    protected void updateUserScore() {
        //הנתונים במסדי המשתמש תוצאת נתוני עדכון
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference maxScoreRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("maxScore");

        UserDatabase dbHelper = new UserDatabase(PlayActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDatabase.AMOUNT_BUTTONS_PRESSED, buttonsPressed);
        String whereClause = UserDatabase.COLUMN_ID + "=?";
        String[] whereArgs = {userId};
        db.update(UserDatabase.TABLE_NAME, values, whereClause, whereArgs);
        db.close();

        buttonsPressed = 0;

        maxScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxScore = dataSnapshot.getValue(Integer.class);
                if (maxScore < game.getScore()) {
                    maxScore = game.getScore();

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
                etScore.setText(String.valueOf(game.getScore()));
                game.setScore(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    protected void createGameOverDialog(){

        try {
            // code to display dialog or toast message
            gameOverDialog = new Dialog(PlayActivity.this);
            gameOverDialog.setContentView(R.layout.gameover_layout);
            gameOverDialog.setTitle("Game Over");
            gameOverDialog.setCancelable(false);
            etFinalScore = gameOverDialog.findViewById(R.id.etFinalScore);
            etFinalScore.setText(Integer.toString(game.getScore()));
            btnGameOverExit = gameOverDialog.findViewById(R.id.btnGameOverExit); // use d.findViewById instead of findViewById
            btnGameOverExit.setOnClickListener(this::onClick);

            gameOverDialog.show();
        } catch (WindowManager.BadTokenException e) {

        }


    }
//endregion
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



