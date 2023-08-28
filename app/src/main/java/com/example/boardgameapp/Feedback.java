package com.example.boardgameapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "feedback_table")
public class Feedback {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private float hostRating;
    private float foodRating;
    private float overallRating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHostRating() {
        return hostRating;
    }

    public void setHostRating(float hostRating) {
        this.hostRating = hostRating;
    }

    public float getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(float foodRating) {
        this.foodRating = foodRating;
    }

    public float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(float overallRating) {
        this.overallRating = overallRating;
    }

}
