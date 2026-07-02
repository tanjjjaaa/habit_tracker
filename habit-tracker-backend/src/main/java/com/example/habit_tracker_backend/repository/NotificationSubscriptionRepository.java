package com.example.habit_tracker_backend.repository;

import com.example.habit_tracker_backend.entity.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {

    List<NotificationSubscription> findByUserId(Long userId);
}