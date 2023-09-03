package com.example.boardgameapp;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FeedbackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFeedback(Feedback feedback);

    @Query("SELECT * FROM feedback_table")
    List<Feedback> getAllFeedback();

    @Query("SELECT AVG(hostRating) FROM feedback_table")
    float getAverageHostRating();

}
