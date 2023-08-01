package com.example.boardgameapp;

public class Spieltermin {
    private String date;
    private String location;

    public Spieltermin(String date, String location) {
        this.date = date;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}
