package com.example.boardgameapp;

public class GameItem {
    private String gameName;
    private int votes;
    private boolean selected; // Hinzugefügt für die Auswahl

    public GameItem(String gameName, int votes) {
        this.gameName = gameName;
        this.votes = votes;
        this.selected = false; // Standardmäßig nicht ausgewählt
    }

    public String getGameName() {
        return gameName;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

