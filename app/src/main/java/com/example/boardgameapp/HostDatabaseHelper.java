package com.example.boardgameapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HostDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "host_games.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PARTICIPANTS = "participants";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAMES + " (" +
                    COLUMN_GAME_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_PARTICIPANTS + " INTEGER)";

    public HostDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // Method to add a new game
    public void addGame(String gameName, String date, int participants) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_NAME, gameName);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_PARTICIPANTS, participants);
        db.insertWithOnConflict(TABLE_GAMES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Method to get all games
    public Cursor getAllGames() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_GAMES, null, null, null, null, null, null);
    }

    // Other methods for updating and deleting games as needed
}
