package com.example.boardgameapp;

public class Spieltermin {
    private String name;

    public Spieltermin(int id, String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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


