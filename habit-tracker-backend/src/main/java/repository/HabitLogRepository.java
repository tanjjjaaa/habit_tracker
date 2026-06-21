package com.example.habit_tracker_backend.repository;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByHabit(Habit habit);
}