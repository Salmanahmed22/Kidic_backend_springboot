package com.example.kidic.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "diseases_and_allergies")
public class DiseaseAndAllergy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private DiseaseAllergyType type;
    
    @Size(max = 1000)
    private String description;
    
    @Size(max = 2000)
    @Column(name = "ai_response")
    private String aiResponse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    @JsonBackReference(value = "child-diseases")
    private Child child;
    
    // Constructors
    public DiseaseAndAllergy() {}
    
    public DiseaseAndAllergy(DiseaseAllergyType type, String description, String aiResponse, Child child) {
        this.type = type;
        this.description = description;
        this.aiResponse = aiResponse;
        this.child = child;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public DiseaseAllergyType getType() {
        return type;
    }
    
    public void setType(DiseaseAllergyType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAiResponse() {
        return aiResponse;
    }
    
    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }
    
    public Child getChild() {
        return child;
    }
    
    public void setChild(Child child) {
        this.child = child;
    }
    
    public enum DiseaseAllergyType {
        ALLERGY, DISEASE, CONDITION, INTOLERANCE, SENSITIVITY, OTHER
    }
}
