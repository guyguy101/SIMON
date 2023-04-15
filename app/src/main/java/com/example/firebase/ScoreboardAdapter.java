package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ViewHolder> {

    Context context;
    private ArrayList<User> userList;

    public ScoreboardAdapter(ArrayList<User> userList,Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scoreboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getNickname());
        holder.scoreTextView.setText(String.valueOf(user.getMaxScore()));
        holder.rankTextView.setText(String.valueOf(position + 1) + "  ");

        if (position == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffd700")); // gold
        } else if (position == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#c0c0c0")); // silver
        } else if (position == 2) {
            holder.itemView.setBackgroundColor(Color.parseColor("#cd7f32")); // bronze
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE); // default
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView scoreTextView;
        public TextView rankTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.name_text_view);
            scoreTextView = view.findViewById(R.id.score_text_view);
            rankTextView = view.findViewById(R.id.rank_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the user at this position
                    User user = userList.get(getAdapterPosition());


                    Intent intent = new Intent(context, ScoreboardUserDetails.class);
                    intent.putExtra("name", user.getNickname());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String joinEuropeanDate = dateFormat.format(user.getDateJoined());
                    String lastEuropeanDate = dateFormat.format(user.getLastDatePlayed());
                    intent.putExtra("date_joined", joinEuropeanDate);
                    intent.putExtra("last_played", lastEuropeanDate);
                    context.startActivity(intent);
                }
            });
        }


    }
}
