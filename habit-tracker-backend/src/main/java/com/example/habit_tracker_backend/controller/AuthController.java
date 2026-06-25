package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.dto.AuthResponse;
import com.example.habit_tracker_backend.dto.LoginRequest;
import com.example.habit_tracker_backend.dto.RegisterRequest;
import com.example.habit_tracker_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        System.out.println("=== AuthController ===");
        System.out.println("Получен запрос на регистрацию");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Password: '" + request.getPassword() + "'");
        System.out.println("Confirm:  '" + request.getConfirmPassword() + "'");
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}