package com.example.boardgameapp;

public class Spieltermin {
    private String vorname;
    private String ort;
    private String spieldt;
    private String email;

    public Spieltermin(String vorname, String ort, String spieldt, String email) {
        this.vorname = vorname;
        this.ort = ort;
        this.spieldt = spieldt;
        this.email = email;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getSpieldt() {
        return spieldt;
    }

    public void setSpieldt(String spieldt) {
        this.spieldt = spieldt;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
