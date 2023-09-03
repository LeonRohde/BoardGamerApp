package com.example.boardgameapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerDatabaseHelper extends SQLiteOpenHelper {
    private String TAG = PlayerDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "player.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PLAYER = "player";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_VORNAME = "vorname";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STR = "strasse";
    private static final String COLUMN_HAUSNR = "hausnr";
    private static final String COLUMN_PLZ = "plz";
    private static final String COLUMN_ORT = "ort";
    private static final String COLUMN_SPIELDT = "spieldt";

    private Context context;
    public PlayerDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_PLAYER + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_VORNAME + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_STR + " TEXT, " +
                COLUMN_HAUSNR + " TEXT, " +
                COLUMN_PLZ + " TEXT, " +
                COLUMN_ORT + " TEXT, " +
                COLUMN_SPIELDT + " TEXT)";
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS player");
        onCreate(db);
    }
    public void addPlayer(String email, String vorname, String name, String str, String hausNr,
                          String plz, String ort){
        SQLiteDatabase dbPlayer = this.getWritableDatabase();
        ContentValues valuesPlayer = new ContentValues();

        valuesPlayer.put(COLUMN_EMAIL, email);
        valuesPlayer.put(COLUMN_VORNAME, vorname);
        valuesPlayer.put(COLUMN_NAME, name);
        valuesPlayer.put(COLUMN_STR, str);
        valuesPlayer.put(COLUMN_HAUSNR, hausNr);
        valuesPlayer.put(COLUMN_PLZ, plz);
        valuesPlayer.put(COLUMN_ORT, ort);
//      Spieldatum bleibt bei einem neuen Spieler leer
        valuesPlayer.put(COLUMN_SPIELDT, "");

        long newPlayer = dbPlayer.insert(TABLE_PLAYER, null, valuesPlayer);
        if (newPlayer == -1) {
            Toast.makeText(context, "Spieler wurde nicht angelegt", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Spieler wurde aufgenommen", Toast.LENGTH_SHORT).show();
        }

        dbPlayer.close();
    }
}
