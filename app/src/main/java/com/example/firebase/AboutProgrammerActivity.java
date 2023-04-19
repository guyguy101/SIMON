package com.example.firebase;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AboutProgrammerActivity extends AppCompatActivity {

Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_programmer);

        LinearLayout aboutLayout = findViewById(R.id.aboutLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) aboutLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.start();


    }

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
                intent = new Intent(this , MainActivity.class);
                startActivity(intent);
                finish();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //endregion
}
