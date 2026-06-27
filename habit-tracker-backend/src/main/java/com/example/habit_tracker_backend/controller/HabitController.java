package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.HabitRepository;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.habit_tracker_backend.dto.CreateHabitRequest;
import java.time.LocalDateTime;
import java.util.List;
import com.example.habit_tracker_backend.entity.HabitLog;
import com.example.habit_tracker_backend.repository.HabitLogRepository;

@RestController
@RequestMapping("/api/habits")
public class HabitController {
    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    // ПРОВЕРКА: просто возвращаем всех пользователей
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ПРОВЕРКА: возвращаем привычки пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getHabitsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Загружаем привычки с логами
        List<Habit> habits = habitRepository.findByUser(user);
        habits.forEach(habit -> {
            // Принудительно загружаем habitLogs, чтобы они попали в JSON
            habit.getHabitLogs().size();
        });

        return ResponseEntity.ok(habits);
    }

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
}