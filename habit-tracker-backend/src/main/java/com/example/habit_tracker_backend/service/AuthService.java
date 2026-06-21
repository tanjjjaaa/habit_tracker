package com.example.habit_tracker_backend.service;

import com.example.habit_tracker_backend.dto.AuthResponse;
import com.example.habit_tracker_backend.dto.LoginRequest;
import com.example.habit_tracker_backend.dto.RegisterRequest;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // РЕГИСТРАЦИЯ (без шифрования)
    public AuthResponse register(RegisterRequest request) {
        // Проверка: пароль и подтверждение совпадают
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Пароли не совпадают");
        }

        // Проверка: логин уже занят
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthResponse(false, "Пользователь с таким логином уже существует");
        }

        // Создаём пользователя
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPasswordHash(request.getPassword()); // ← Пароль без шифрования!
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        return new AuthResponse(
                true,
                "Регистрация успешна!",
                saved.getId(),
                saved.getUsername(),
                saved.getFullName()
        );
    }

    // ВХОД (без шифрования)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new AuthResponse(false, "Неверный логин или пароль");
        }

        // Сравниваем пароли в открытом виде
        if (!request.getPassword().equals(user.getPasswordHash())) {
            return new AuthResponse(false, "Неверный логин или пароль");
        }

        return new AuthResponse(
                true,
                "Вход выполнен!",
                user.getId(),
                user.getUsername(),
                user.getFullName()
        );
    }
}