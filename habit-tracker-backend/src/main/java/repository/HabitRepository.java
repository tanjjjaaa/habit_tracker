package com.example.habit_tracker_backend.repository;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
}