package com.example.firebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OpenActivity extends AppCompatActivity implements View.OnClickListener {
Button btnPlay;
Button btnScoreboard;
Button btnEasyMode;
Button btnHardMode;
Dialog gameModeDialog;
FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;
Intent intent;
public static int GAME_MODE_CODE; //0 means easy mode, 1 means hard mode
private WifiReceiver wifiReceiver = new WifiReceiver();
private UserDatabase dbHelper = new UserDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_simon);

        LinearLayout linearLayout = findViewById(R.id.openingLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.start();


        btnPlay= findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnScoreboard=findViewById(R.id.btnScoreboard);
        btnScoreboard.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();



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



    @Override
    public void onClick(View view) {
        if(view==btnPlay){
            ChooseGameMode();
        }
        if(view == btnEasyMode){
            GAME_MODE_CODE =0;
            gameModeDialog.dismiss();
            intent = new Intent(this,PlayActivity.class);
            startActivity(intent);
            finish();
        }
        if(view == btnHardMode){
            GAME_MODE_CODE = 1;
            gameModeDialog.dismiss();
            intent = new Intent(this,PlayActivity.class);
            startActivity(intent);
            finish();
        }
        if(view == btnScoreboard){
            intent= new Intent(this,ScoreboardActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void ChooseGameMode(){
        gameModeDialog = new Dialog(this);
        gameModeDialog.setContentView(R.layout.gamemode_layout);
        gameModeDialog.setTitle("Game Over");
        gameModeDialog.setCancelable(false);
        btnEasyMode = gameModeDialog.findViewById(R.id.btnEasyMode);
        btnEasyMode.setOnClickListener(this::onClick);
        btnHardMode = gameModeDialog.findViewById(R.id.btnHardMode); // use d.findViewById instead of findViewById
        btnHardMode.setOnClickListener(this::onClick);

        gameModeDialog.show();

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void logout(){
        firebaseAuth.signOut();
        intent= new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.settings_menu, menu);

        return true;


    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exitItem:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Application")
                        .setMessage("Are you sure you want to close `Simon?`")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the current date
                                if(firebaseUser!= null){
                                    Date currentDate = new Date();

                                    // Set the lastDatePlayed field in Firebase
                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                    userRef.child("lastDatePlayed").setValue(currentDate);

                                    // Set the date in SQLite database

                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(UserDatabase.COLUMN_LAST_DATE_PLAYED, getCurrentDate());
                                    String whereClause = UserDatabase.COLUMN_EMAIL + " = ?";
                                    String[] whereArgs = {firebaseAuth.getCurrentUser().getEmail()};
                                    db.update(UserDatabase.TABLE_NAME, values, whereClause, whereArgs);
                                    db.close();
                                }


                                finishAndRemoveTask();
                                finishAffinity();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.logoutItem:
                logout();
                return true;
            case R.id.aboutProgramItem:
                intent = new Intent(this, AboutProgrammerActivity.class);
                startActivity(intent);
                finish();
            case R.id.aboutAppItem:
                intent = new Intent(this,AboutAppActivity.class);
                startActivity(intent);
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //endregion
}
