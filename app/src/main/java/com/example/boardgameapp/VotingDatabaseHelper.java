package com.example.boardgameapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VotingDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "voting.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_VOTES = "votes";
    public static final String COLUMN_GAME = "game";
    public static final String COLUMN_VOTES = "votes";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_VOTES + " (" +
                    COLUMN_GAME + " TEXT PRIMARY KEY, " +
                    COLUMN_VOTES + " INTEGER)";

    public VotingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTES);
        onCreate(db);
    }
}
