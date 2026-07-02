package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.HabitRepository;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.habit_tracker_backend.dto.CreateHabitRequest;
import java.util.stream.Collectors;
import com.example.habit_tracker_backend.dto.HabitResponseDTO;
import com.example.habit_tracker_backend.dto.HabitLogDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public ResponseEntity<List<HabitResponseDTO>> getHabitsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Habit> habits = habitRepository.findByUser(user);

        List<HabitResponseDTO> response = habits.stream().map(habit -> {
            HabitResponseDTO dto = new HabitResponseDTO();
            dto.setId(habit.getId());
            dto.setTitle(habit.getTitle());
            dto.setDescription(habit.getDescription());
            dto.setColor(habit.getColor());
            dto.setFrequencyType(habit.getFrequencyType());
            dto.setFrequencyDays(habit.getFrequencyDays());
            dto.setPlantType(habit.getPlantType());
            dto.setPlantStage(habit.getPlantStage());
            dto.setCreatedAt(habit.getCreatedAt());
            dto.setIsActive(habit.getIsActive());

            // Преобразуем список логов
            List<HabitLogDTO> logDTOs = habit.getHabitLogs().stream().map(log -> {
                HabitLogDTO logDTO = new HabitLogDTO();
                logDTO.setId(log.getId());
                logDTO.setCompletedAt(log.getCompletedAt());
                logDTO.setNote(log.getNote());
                return logDTO;
            }).collect(Collectors.toList());

            dto.setHabitLogs(logDTOs);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
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
        habit.setCreatedAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));        habit.setIsActive(true);

        Habit saved = habitRepository.save(habit);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<HabitLog> completeHabit(@PathVariable Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        HabitLog log = new HabitLog();
        log.setHabit(habit);
        log.setCompletedAt(LocalDate.now(ZoneId.of("Europe/Moscow")));
        HabitLog saved = habitLogRepository.save(log);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long habitId) {

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        // Сначала удаляем все отметки выполнения
        habitLogRepository.deleteAll(habit.getHabitLogs());

        // Затем удаляем привычку
        habitRepository.delete(habit);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{habitId}/log")
    public ResponseEntity<Void> deleteHabitLog(@PathVariable Long habitId) {

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        LocalDate today = LocalDate.now(ZoneId.of("Europe/Moscow"));

        habitLogRepository.findByHabitAndCompletedAt(habit, today)
                .ifPresent(habitLogRepository::delete);

        return ResponseEntity.noContent().build();


    }

}