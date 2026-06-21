package com.example.habit_tracker_backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 7)
    private String color;

    @Column(name = "frequency_type", nullable = false, length = 20)
    private String frequencyType;

    @Column(name = "frequency_days")
    private Integer[] frequencyDays;

    @Column(name = "plant_type")
    private Integer plantType;

    @Column(name = "plant_stage")
    private Integer plantStage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HabitLog> habitLogs;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public List<HabitLog> getHabitLogs() { return habitLogs; }
    public void setHabitLogs(List<HabitLog> habitLogs) { this.habitLogs = habitLogs; }
}