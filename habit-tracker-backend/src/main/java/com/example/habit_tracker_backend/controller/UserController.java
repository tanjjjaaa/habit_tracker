package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.dto.UpdateUserRequest;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // обновление имени
        user.setFullName(request.getFullName());

        // включение/выключение уведомлений
        user.setNotificationsEnabled(request.getNotificationsEnabled());

        // время уведомлений
        if (request.getNotificationTime() != null) {
            user.setNotificationTime(
                    LocalTime.parse(request.getNotificationTime())
            );
        }

        return ResponseEntity.ok(userRepository.save(user));
    }
}