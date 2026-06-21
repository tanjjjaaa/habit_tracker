package com.example.habit_tracker_backend.dto;

public class DayProgress {
    private int day;
    private boolean completed;

    public DayProgress(int day, boolean completed) {
        this.day = day;
        this.completed = completed;
    }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}