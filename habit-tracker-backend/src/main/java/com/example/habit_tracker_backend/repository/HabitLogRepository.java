package com.example.habit_tracker_backend.repository;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findByHabit(Habit habit);

    Optional<HabitLog> findByHabitAndCompletedAt(Habit habit, LocalDate completedAt);
}

//public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
//
//    List<HabitLog> findByHabit(Habit habit);
//
//    Optional<HabitLog> findByHabitAndCompletedAt(Habit habit, LocalDateTime completedAt);
//}