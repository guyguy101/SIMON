package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_DIFFICULTY = "difficulty";
    private static final int DEFAULT_DIFFICULTY = 0; // 0 for Easy, 1 for Hard

    private SharedPreferences settings;
    private RadioGroup radioGroupDifficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(PREFS_NAME, 0);
        int difficulty = settings.getInt(PREF_DIFFICULTY, DEFAULT_DIFFICULTY);
        // Set the selected difficulty based on the stored preference value
        RadioButton selectedDifficultyButton = findViewById(difficulty == 0 ? R.id.radio_button_easy : R.id.radio_button_hard);
        selectedDifficultyButton.setChecked(true);
    }


}