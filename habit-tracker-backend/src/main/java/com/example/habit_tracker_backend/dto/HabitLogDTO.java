package com.example.habit_tracker_backend.dto;

import java.time.LocalDate;

public class HabitLogDTO {
    private Long id;
    private LocalDate completedAt;
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDate completedAt) { this.completedAt = completedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}