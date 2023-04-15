package com.example.firebase;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreboardActivity extends AppCompatActivity {
    private ArrayList<User> userList = new ArrayList<>();

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
}
