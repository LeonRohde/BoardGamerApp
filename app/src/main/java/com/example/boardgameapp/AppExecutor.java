package com.example.boardgameapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final Object LOCK = new Object();
    private static AppExecutor instance;
    private final Executor diskIO;

    private AppExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static AppExecutor getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }
}
