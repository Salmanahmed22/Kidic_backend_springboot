package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "educational_contents")
public class EducationalContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @NotBlank
    @Size(max = 500)
    private String link;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "estimated_time")
    private String estimatedTime;
    
    @Size(max = 1000)
    private String description;
    
    // Constructors
    public EducationalContent() {}
    
    public EducationalContent(String title, String link, String estimatedTime, String description) {
        this.title = title;
        this.link = link;
        this.estimatedTime = estimatedTime;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
