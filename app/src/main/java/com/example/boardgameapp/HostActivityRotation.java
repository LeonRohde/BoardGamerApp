package com.example.boardgameapp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HostActivityRotation extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HostRotationAdapter adapter;
    private ArrayList<Player> playerList;
    private HostDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_rotation);

        databaseHelper = new HostDatabaseHelper(this); // Füge diese Zeile hinzu

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerList = new ArrayList<>();
        adapter = new HostRotationAdapter(playerList);
        recyclerView.setAdapter(adapter);

        loadPlayerData();
    }

    private void loadPlayerData() {
        // Get the game data from the database
        Cursor cursor = databaseHelper.getAllGames();

        if (cursor.moveToFirst()) {
            do {
                String gameName = cursor.getString(cursor.getColumnIndex(HostDatabaseHelper.COLUMN_GAME_NAME));
                // Add your logic to retrieve other game details like date and participants

                playerList.add(new Player(gameName, false)); // Example: You can adjust this part based on your data structure
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
