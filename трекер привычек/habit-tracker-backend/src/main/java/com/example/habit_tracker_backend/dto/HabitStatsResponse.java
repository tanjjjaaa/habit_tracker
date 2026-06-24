package com.example.habit_tracker_backend.dto;

public class HabitStatsResponse {
    private Long habitId;
    private String title;
    private int streak;              // текущая серия
    private int totalCompletions;    // всего выполнений
    private int requiredDays;        // сколько дней должно было быть выполнено
    private double completionRate;   // процент выполнения
    private int daysSinceCreation;   // дней с создания

    // Конструктор
    public HabitStatsResponse(Long habitId, String title, int streak, int totalCompletions,
                              int requiredDays, double completionRate, int daysSinceCreation) {
        this.habitId = habitId;
        this.title = title;
        this.streak = streak;
        this.totalCompletions = totalCompletions;
        this.requiredDays = requiredDays;
        this.completionRate = completionRate;
        this.daysSinceCreation = daysSinceCreation;
    }

    // Геттеры
    public Long getHabitId() { return habitId; }
    public String getTitle() { return title; }
    public int getStreak() { return streak; }
    public int getTotalCompletions() { return totalCompletions; }
    public int getRequiredDays() { return requiredDays; }
    public double getCompletionRate() { return completionRate; }
    public int getDaysSinceCreation() { return daysSinceCreation; }
}