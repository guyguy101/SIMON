package com.example.firebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpenActivity extends AppCompatActivity implements View.OnClickListener {
Button btnPlay;
Button btnScoreboard;

FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;
Intent intent;

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




    @Override
    public void onClick(View view) {
        if(view==btnPlay){
            intent= new Intent(this,PlayActivity.class);
            startActivity(intent);
            finish();
        }

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

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //endregion
}
