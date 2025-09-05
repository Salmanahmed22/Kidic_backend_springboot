package com.example.kidic.repository;

import com.example.kidic.entity.Notification;
import com.example.kidic.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByFamily(Family family);
    
    @Query("SELECT n FROM Notification n WHERE n.family.id = :familyId")
    List<Notification> findByFamilyId(@Param("familyId") UUID familyId);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type")
    List<Notification> findByType(@Param("type") Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.isRead = :isRead")
    List<Notification> findByIsRead(@Param("isRead") Boolean isRead);
    
    @Query("SELECT n FROM Notification n WHERE n.family.id = :familyId AND n.isRead = :isRead")
    List<Notification> findByFamilyIdAndIsRead(@Param("familyId") UUID familyId, @Param("isRead") Boolean isRead);
    
    long countByFamily(Family family);
    
    long countByFamilyAndIsRead(Family family, Boolean isRead);
}
