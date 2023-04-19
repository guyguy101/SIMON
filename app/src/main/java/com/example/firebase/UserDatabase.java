package com.example.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_MAX_SCORE = "max_score";
    public static final String COLUMN_DATE_JOINED = "last_date_joined";
    public static final String COLUMN_LAST_DATE_PLAYED = "last_date_played";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_NICKNAME + " TEXT NOT NULL, " +
                    COLUMN_MAX_SCORE + " INTEGER DEFAULT 0, " +
                    COLUMN_DATE_JOINED + " TEXT, " +
                    COLUMN_LAST_DATE_PLAYED + " TEXT);";

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_NICKNAME + " TEXT," +
                COLUMN_MAX_SCORE + " INTEGER DEFAULT 0," +
                COLUMN_DATE_JOINED + " TEXT," +
                COLUMN_LAST_DATE_PLAYED + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //הנתונים מסד את המעדכנת פעולה
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
