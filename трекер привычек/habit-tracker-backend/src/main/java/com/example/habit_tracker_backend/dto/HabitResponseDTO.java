package com.example.habit_tracker_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HabitResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String color;
    private String frequencyType;
    private Integer[] frequencyDays;
    private Integer plantType;
    private Integer plantStage;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<HabitLogDTO> habitLogs;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<HabitLogDTO> getHabitLogs() { return habitLogs; }
    public void setHabitLogs(List<HabitLogDTO> habitLogs) { this.habitLogs = habitLogs; }
}