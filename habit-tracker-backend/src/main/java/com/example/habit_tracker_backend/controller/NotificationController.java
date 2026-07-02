package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.entity.NotificationSubscription;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;
import com.example.habit_tracker_backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserRepository userRepository;

    public NotificationController(NotificationService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping("/settings")
    public ResponseEntity<?> saveSettings(@RequestBody SettingsRequest request) {
        try {
            System.out.println("📝 Настройки получены:");
            System.out.println("  userId: " + request.getUserId());
            System.out.println("  enabled: " + request.getEnabled());
            System.out.println("  time: " + request.getTime());

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (request.getEnabled() != null) {
                user.setNotificationsEnabled(request.getEnabled());
            }
            if (request.getTime() != null && !request.getTime().isEmpty()) {
                user.setNotificationTime(LocalTime.parse(request.getTime()));
            }

            User updated = userRepository.save(user);
            System.out.println("✅ Настройки сохранены для пользователя: " + updated.getId());

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.err.println("❌ Ошибка сохранения настроек: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

//    // ===== СОХРАНЕНИЕ НАСТРОЕК УВЕДОМЛЕНИЙ =====
//    @PostMapping("/settings")
//    public ResponseEntity<?> saveSettings(@RequestBody SettingsRequest request) {
//        try {
//            System.out.println("📝 Настройки получены:");
//            System.out.println("  userId: " + request.getUserId());
//            System.out.println("  enabled: " + request.getNotificationsEnabled());
//            System.out.println("  time: " + request.getNotificationTime());
//
//            User user = userRepository.findById(request.getUserId())
//                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
//
//            if (request.getNotificationsEnabled() != null) {
//                user.setNotificationsEnabled(request.getNotificationsEnabled());
//            }
//            if (request.getNotificationTime() != null && !request.getNotificationTime().isEmpty()) {
//                user.setNotificationTime(LocalTime.parse(request.getNotificationTime()));
//            }
//
//            User updated = userRepository.save(user);
//            System.out.println("✅ Настройки сохранены для пользователя: " + updated.getId());
//
//            return ResponseEntity.ok(updated);
//
//        } catch (Exception e) {
//            System.err.println("❌ Ошибка сохранения настроек: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
//        }
//    }

    // ===== СОХРАНЕНИЕ ПОДПИСКИ =====
    @PostMapping("/subscribe")
    public void subscribe(@RequestBody SubscribeRequest request) {
        NotificationSubscription sub = new NotificationSubscription();
        sub.setUserId(request.getUserId());
        sub.setEndpoint(request.getSubscription().getEndpoint());
        sub.setP256dh(request.getSubscription().getKeys().getP256dh());
        sub.setAuth(request.getSubscription().getKeys().getAuth());

        service.saveSubscription(sub);
        System.out.println("✅ Подписка сохранена в БД");
    }

    // ===== ТЕСТОВАЯ ОТПРАВКА =====
    @PostMapping("/send-test/{userId}")
    public void sendTest(@PathVariable Long userId) throws Exception {
        String payload = "{\"title\":\"Напоминание\",\"body\":\"Пора выполнить привычку!\"}";
        service.sendToUser(userId, payload);
        System.out.println("✅ Уведомления отправлены");
    }



    // ===== DTO ДЛЯ ПОДПИСКИ =====
    public static class SubscribeRequest {
        private Long userId;
        private PushSubscription subscription;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public PushSubscription getSubscription() { return subscription; }
        public void setSubscription(PushSubscription subscription) { this.subscription = subscription; }
    }

    public static class PushSubscription {
        private String endpoint;
        private Keys keys;

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public Keys getKeys() { return keys; }
        public void setKeys(Keys keys) { this.keys = keys; }
    }

    public static class Keys {
        private String p256dh;
        private String auth;

        public String getP256dh() { return p256dh; }
        public void setP256dh(String p256dh) { this.p256dh = p256dh; }

        public String getAuth() { return auth; }
        public void setAuth(String auth) { this.auth = auth; }
    }

    public static class SettingsRequest {
        private Long userId;
        private Boolean enabled;        // ← соответствует enabled из фронтенда
        private String time;            // ← соответствует time из фронтенда
        // habits не нужно

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
    }
}