package com.example.habit_tracker_backend.dto;

public class CreateHabitRequest {
    private Long userId;
    private String title;
    private String description;
    private String color;
    private String frequencyType;
    private Integer[] frequencyDays;
    private Integer plantType;
    private Integer plantStage;

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getFrequencyType() { return frequencyType; }
    public void setFrequencyType(String frequencyType) { this.frequencyType = frequencyType; }

    public Integer[] getFrequencyDays() { return frequencyDays; }
    public void setFrequencyDays(Integer[] frequencyDays) { this.frequencyDays = frequencyDays; }

    public Integer getPlantType() { return plantType; }
    public void setPlantType(Integer plantType) { this.plantType = plantType; }

    public Integer getPlantStage() { return plantStage; }
    public void setPlantStage(Integer plantStage) { this.plantStage = plantStage; }
}