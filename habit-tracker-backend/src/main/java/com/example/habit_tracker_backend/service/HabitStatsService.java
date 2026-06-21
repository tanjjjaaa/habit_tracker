package com.example.habit_tracker_backend.service;

import com.example.habit_tracker_backend.dto.HabitStatsResponse;
import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.HabitLog;
import com.example.habit_tracker_backend.repository.HabitLogRepository;
import com.example.habit_tracker_backend.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.habit_tracker_backend.dto.DayProgress;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HabitStatsService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private UserRepository userRepository;

    public HabitStatsResponse getStats(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        // 1. Все логи выполнения
        List<HabitLog> logs = habitLogRepository.findByHabit(habit);
        int totalCompletions = logs.size();

        // 2. Дней с создания
        LocalDate createdDate = habit.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();
        int daysSinceCreation = (int) ChronoUnit.DAYS.between(createdDate, today) + 1;

        // 3. Сколько дней должно было быть выполнено (с учётом частоты)
        int requiredDays = calculateRequiredDays(habit, createdDate, today);

        // 4. Процент выполнения
        double completionRate = requiredDays > 0 ?
                (double) totalCompletions / requiredDays * 100 : 0;

        // 5. Серия (streak)
        int streak = calculateStreak(habit, logs);

        return new HabitStatsResponse(
                habitId,
                habit.getTitle(),
                streak,
                totalCompletions,
                requiredDays,
                Math.min(completionRate, 100), // не больше 100%
                daysSinceCreation
        );
    }

    // Расчёт серии (сколько дней подряд выполнено)
    private int calculateStreak(Habit habit, List<HabitLog> logs) {
        if (logs.isEmpty()) return 0;

        // Собираем даты выполнения в Set
        java.util.Set<LocalDate> completionDates = new java.util.HashSet<>();
        for (HabitLog log : logs) {
            completionDates.add(log.getCompletedAt().toLocalDate());
        }

        LocalDate today = LocalDate.now();
        int streak = 0;

        // Идём назад от сегодня
        for (int i = 0; i < 365; i++) {
            LocalDate date = today.minusDays(i);
            // Проверяем, должна ли привычка выполняться в этот день
            if (shouldExecuteOnDate(habit, date)) {
                if (completionDates.contains(date)) {
                    streak++;
                } else {
                    break; // первый пропуск — серия прервалась
                }
            }
            // Если привычка не должна выполняться — просто пропускаем
        }

        return streak;
    }

    // Должна ли привычка выполняться в конкретную дату
    private boolean shouldExecuteOnDate(Habit habit, LocalDate date) {
        String type = habit.getFrequencyType();

        if ("daily".equals(type)) {
            return true; // каждый день
        }

        if ("weekly".equals(type)) {
            Integer[] days = habit.getFrequencyDays();
            if (days == null || days.length == 0) return false;
            int dayOfWeek = date.getDayOfWeek().getValue() - 1; // 0=пн, 6=вс
            for (int d : days) {
                if (d == dayOfWeek) return true;
            }
            return false;
        }

        if ("monthly".equals(type)) {
            Integer[] days = habit.getFrequencyDays();
            if (days == null || days.length == 0) return false;
            int dayOfMonth = date.getDayOfMonth();
            for (int d : days) {
                if (d == dayOfMonth) return true;
            }
            return false;
        }

        return false;
    }

    // Сколько дней должно было быть выполнено
    private int calculateRequiredDays(Habit habit, LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (shouldExecuteOnDate(habit, current)) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    public List<DayProgress> getMonthProgress(Long userId, int year, int month) {
        // 1. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Получаем все привычки пользователя
        List<Habit> habits = habitRepository.findByUser(user);

        // 3. Для каждого дня месяца проверяем, выполнена ли хотя бы одна привычка
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<DayProgress> result = new ArrayList<>();

        // 4. Проходим по дням месяца
        for (int day = 1; day <= endDate.getDayOfMonth(); day++) {
            LocalDate currentDate = LocalDate.of(year, month, day);
            boolean completed = false;

            // Проверяем, есть ли хоть одна выполненная привычка в этот день
            for (Habit habit : habits) {
                // Проверяем, должна ли привычка выполняться в этот день
                if (shouldExecuteOnDate(habit, currentDate)) {
                    // Проверяем, выполнена ли она
                    if (isHabitCompletedOnDate(habit, currentDate)) {
                        completed = true;
                        break;
                    }
                }
            }

            result.add(new DayProgress(day, completed));
        }

        return result;
    }

    // Проверяем, выполнена ли привычка в конкретный день
    private boolean isHabitCompletedOnDate(Habit habit, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<HabitLog> logs = habitLogRepository.findByHabit(habit);
        for (HabitLog log : logs) {
            LocalDateTime completedAt = log.getCompletedAt();
            if (!completedAt.isBefore(startOfDay) && !completedAt.isAfter(endOfDay)) {
                return true;
            }
        }
        return false;
    }
}