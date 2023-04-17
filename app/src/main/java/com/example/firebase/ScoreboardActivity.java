package com.example.firebase;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScoreboardActivity extends AppCompatActivity {
    private ArrayList<User> userList = new ArrayList<>();
    private WifiReceiver wifiReceiver = new WifiReceiver();
    private Intent intent;
    private UserDatabase dbHelper = new UserDatabase(this);
    FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,OpenActivity.class));
        finish();
    }
    public void sortUsers() { //bubble sort
        for (int i = 0; i < userList.size() - 1; i++) {
            for (int j = i + 1; j < userList.size(); j++) {
                if (userList.get(j).getMaxScore() > userList.get(i).getMaxScore()) {
                    // swap elements
                    User temp = userList.get(i);
                    userList.set(i, userList.get(j));
                    userList.set(j, temp);
                }
            }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        ScoreboardAdapter adapter = new ScoreboardAdapter(userList,this);
        RecyclerView recyclerView = findViewById(R.id.scoreboard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                sortUsers();

                adapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });



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

    private void logout(){
        firebaseAuth.signOut();
    }

}
