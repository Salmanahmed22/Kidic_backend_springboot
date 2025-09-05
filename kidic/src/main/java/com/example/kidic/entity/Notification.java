package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @NotBlank
    @Size(max = 1000)
    private String content;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    // Constructors
    public Notification() {}
    
    public Notification(NotificationType type, String content, Family family) {
        this.type = type;
        this.content = content;
        this.family = family;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Family getFamily() {
        return family;
    }
    
    public void setFamily(Family family) {
        this.family = family;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public enum NotificationType {
        MEDICAL, EDUCATIONAL, MEAL, GROWTH, GENERAL, URGENT
    }
}
