package com.example.boardgameapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;





public class HostActivityRotation extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HostRotationAdapter adapter;
    private ArrayList<Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_rotation);

        // Initialisieren der RecyclerView und des Adapters
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerList = new ArrayList<>();
        adapter = new HostRotationAdapter(playerList);
        recyclerView.setAdapter(adapter);

        // Hier rufen wir die Methode auf, um die Daten der Spieler zu laden
        loadPlayerData();
    }

    // Dummy-Methode, um die Spielerdaten zu laden
    private void loadPlayerData() {
        // Hier würden Sie normalerweise die Spielerdaten aus der Datenbank oder einem Backend abrufen
        // In diesem Beispiel verwenden wir statische Dummy-Daten
        playerList.add(new Player("Spieler 1", true));
        playerList.add(new Player("Spieler 2", false));
        playerList.add(new Player("Spieler 3", false));
        playerList.add(new Player("Spieler 4", false));

        // Benachrichtige den Adapter über die Änderung der Daten
        adapter.notifyDataSetChanged();
    }
}

