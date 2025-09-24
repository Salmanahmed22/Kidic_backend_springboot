package com.example.kidic.service;
import com.example.kidic.entity.Family;
import com.example.kidic.entity.Notification;
import com.example.kidic.entity.Parent;
import com.example.kidic.repository.FamilyRepository;
import com.example.kidic.repository.NotificationRepository;
import com.example.kidic.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private FamilyRepository familyRepository;


    @Transactional
    public void createNotification(UUID familyId, String message, Notification.NotificationType type) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(()-> new RuntimeException("family not found"));
        List<Parent> parents = family.getParents();
        for (Parent parent : parents) {
            Notification notification = new Notification();
            notification.setParentId(parent.getId());
            notification.setContent(message);
            notification.setType(type);
            notificationRepository.save(notification);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(parent.getId()),
                    "/queue/notifications",
                    notification
            );
        }
    }

    public List<Notification> getUserNotifications(Long parentId) {
        return notificationRepository.findByParentIdOrderByCreatedAtDesc(parentId);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void createBroadcastNotificationsForAllUsers(String message, Notification.NotificationType type) {
        List<Parent> parents = parentRepository.findAll();

        for (Parent parent : parents) {
            Notification notification = new Notification();
            notification.setParentId(parent.getId());
            notification.setContent(message);
            notification.setType(type);
            notificationRepository.save(notification);

            // Send via WebSocket to each user
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(parent.getId()),
                    "/queue/notifications",
                    notification
            );
        }
    }


    public List<Notification> getUnreadNotifications(Long parentId) {
        return notificationRepository.findByUserIdAndIsRead(parentId,false);
    }
}
