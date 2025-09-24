package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.GENERAL;
    
    @NotBlank
    @Size(max = 1000)
    private String content;

    private Long parentId;
    
    @Column(name = "is_read")
    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    
    public enum NotificationType {
        MEDICAL, EDUCATIONAL, MEAL, GROWTH, GENERAL, URGENT
    }
}
