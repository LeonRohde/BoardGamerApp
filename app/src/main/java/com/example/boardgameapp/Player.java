package com.example.boardgameapp;

public class Player {
    private String name;
    private boolean isHost;

    public Player(String name, boolean isHost) {
        this.name = name;
        this.isHost = isHost;
    }

    public String getName() {
        return name;
    }

    public boolean isHost() {
        return isHost;
    }
}
