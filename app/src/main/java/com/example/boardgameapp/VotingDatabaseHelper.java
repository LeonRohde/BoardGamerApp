package com.example.boardgameapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class VotingDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "voting.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    public static final String TABLE_VOTES = "votes";
    public static final String COLUMN_GAME = "game";
    public static final String COLUMN_VOTES = "votes";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_VOTES + " (" +
                    COLUMN_GAME + " TEXT PRIMARY KEY, " +
                    COLUMN_VOTES + " INTEGER)";

    public VotingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context =context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME, "Game1");
        values.put(COLUMN_VOTES, 0);
        long newGame =db.insert(TABLE_VOTES, null, values);
        if (newGame == -1) {
            Toast.makeText(context, "Game nicht angelegt", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Game wurde aufgenommen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("GameVotingActivity", "Tabelle löschen");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTES);
        onCreate(db);
    }

    public void onVoting(String Game){
        SQLiteDatabase db = this.getWritableDatabase();
        String voting =
                "UPDATE " + TABLE_VOTES +
                 " SET " + COLUMN_VOTES + " = " + COLUMN_VOTES + " + 1 " +
                "WHERE " + COLUMN_GAME + " = '" + Game + "';";
        Log.d("GameVotingActivity", voting);
        db.execSQL(voting);
    }
}
