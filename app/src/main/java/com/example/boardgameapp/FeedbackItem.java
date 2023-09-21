package com.example.boardgameapp;
public class FeedbackItem {
    private String Spieler;
    private float Gastgeber;
    private float Essen;
    private float Abend;

    public FeedbackItem(String Spieler, float Gastgeber, float Essen, float Abend) {
        this.Spieler = Spieler;
        this.Gastgeber = Gastgeber;
        this.Essen = Essen;
        this.Abend = Abend;
    }

    public String getSpieler() {
        return Spieler;
    }

    public float getGastgeber() {
        return Gastgeber;
    }

    public float getEssen() {
        return Essen;
    }

    public float getAbend() {
        return Abend;
    }
}
