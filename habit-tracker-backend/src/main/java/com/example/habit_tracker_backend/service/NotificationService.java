package com.example.habit_tracker_backend.service;

import com.example.habit_tracker_backend.entity.NotificationSubscription;
import com.example.habit_tracker_backend.repository.NotificationSubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationService {

    private final NotificationSubscriptionRepository repository;

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    public NotificationService(NotificationSubscriptionRepository repository) {
        this.repository = repository;
    }



    public void saveSubscription(NotificationSubscription subscription) {
        repository.save(subscription);
    }

    public void sendToUser(Long userId, String payload) throws Exception {

        List<NotificationSubscription> subs =
                repository.findByUserId(userId);

        PushService pushService = new PushService();
        pushService.setPublicKey(publicKey);
        pushService.setPrivateKey(privateKey);
        pushService.setSubject("mailto:test@example.com");

        for (NotificationSubscription s : subs) {

            Subscription.Keys keys = new Subscription.Keys(
                    s.getP256dh(),
                    s.getAuth()
            );

            Subscription subscription =
                    new Subscription(s.getEndpoint(), keys);

            Notification notification =
                    new Notification(subscription, payload);

            pushService.send(notification);
        }
    }
}