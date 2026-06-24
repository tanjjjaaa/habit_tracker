package com.example.habit_tracker_backend.dto;

import java.time.LocalDateTime;

public class HabitLogDTO {
    private Long id;
    private LocalDateTime completedAt;
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}