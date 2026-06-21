package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.dto.CreateHabitRequest;
import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.HabitLog;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.HabitLogRepository;
import com.example.habit_tracker_backend.repository.HabitRepository;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.habit_tracker_backend.dto.HabitStatsResponse;
import com.example.habit_tracker_backend.service.HabitStatsService;
import java.time.LocalDateTime;
import java.util.List;
import com.example.habit_tracker_backend.dto.DayProgress;
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;   // ← ДОБАВЛЕНО!

    // Получить привычки пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getHabitsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return ResponseEntity.ok(habitRepository.findByUser(user));
    }

    // Создать привычку
    @PostMapping
    public ResponseEntity<Habit> createHabit(@RequestBody CreateHabitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Habit habit = new Habit();
        habit.setUser(user);
        habit.setTitle(request.getTitle());
        habit.setDescription(request.getDescription());
        habit.setColor(request.getColor());
        habit.setFrequencyType(request.getFrequencyType());
        habit.setFrequencyDays(request.getFrequencyDays());
        habit.setPlantType(request.getPlantType());
        habit.setPlantStage(request.getPlantStage() != null ? request.getPlantStage() : 0);
        habit.setCreatedAt(LocalDateTime.now());
        habit.setIsActive(true);

        Habit saved = habitRepository.save(habit);
        return ResponseEntity.ok(saved);
    }

    // Отметить привычку выполненной
    @PostMapping("/{habitId}/complete")
    public ResponseEntity<HabitLog> completeHabit(@PathVariable Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        HabitLog log = new HabitLog();
        log.setHabit(habit);
        log.setCompletedAt(LocalDateTime.now());

        HabitLog saved = habitLogRepository.save(log);
        return ResponseEntity.ok(saved);
    }

    @Autowired
    private HabitStatsService habitStatsService;

    @GetMapping("/{habitId}/stats")
    public ResponseEntity<HabitStatsResponse> getHabitStats(@PathVariable Long habitId) {
        HabitStatsResponse stats = habitStatsService.getStats(habitId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}/month")
    public ResponseEntity<List<DayProgress>> getMonthProgress(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        List<DayProgress> progress = habitStatsService.getMonthProgress(userId, year, month);
        return ResponseEntity.ok(progress);
    }
}