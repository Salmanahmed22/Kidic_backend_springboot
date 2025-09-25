package com.example.kidic.controller;

import com.example.kidic.config.JwtService;
import com.example.kidic.entity.Notification;
import com.example.kidic.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtService jwtService;


    // Send + store a notification
    @PostMapping("/send")
    public String sendNotification(@RequestHeader("Authorization") String authHeader,
                                         @RequestParam String message,
                                         @RequestParam(defaultValue = "INFO") Notification.NotificationType type) {
        String token = authHeader.substring(7);
        UUID familyId = jwtService.extractFamilyId(token);
        notificationService.createNotification(familyId, message, type);
        return "notification sent";
    }

    // Send broadcast notification
    @PostMapping("/broadcast")
    public String broadcastNotification(@RequestParam String message,
                                              @RequestParam(defaultValue = "INFO") Notification.NotificationType type) {
        notificationService.createBroadcastNotificationsForAllUsers(message, type);
        return "notifications broadcasted";

    }


    // Get all notifications for a user
    @GetMapping("/{parentId}")
    public List<Notification> getNotifications(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long parentId) {
        String token = authHeader.substring(7);
        UUID familyId = jwtService.extractFamilyId(token);
        return notificationService.getUserNotifications(parentId,familyId);
    }

    @GetMapping("/unread/{parentId}")
    public List<Notification> getUnreadNotifications(@RequestHeader("Authorization") String authHeader,
                                                     @PathVariable Long parentId) {
        String token = authHeader.substring(7);
        UUID familyId = jwtService.extractFamilyId(token);
        return notificationService.getUnreadNotifications(parentId);
    }

    // Mark as read
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}

