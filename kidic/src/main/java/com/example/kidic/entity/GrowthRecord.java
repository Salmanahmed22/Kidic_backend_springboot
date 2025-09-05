package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "growth_records")
public class GrowthRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(max = 1000)
    @Column(name = "additional_info")
    private String additionalInfo;
    
    @NotNull
    @Column(name = "date_of_record")
    private LocalDate dateOfRecord;
    
    private Double height;
    
    private Double weight;
    
    @Enumerated(EnumType.STRING)
    private GrowthType type;
    
    @Enumerated(EnumType.STRING)
    private StatusType status;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;
    
    // Constructors
    public GrowthRecord() {}
    
    public GrowthRecord(String additionalInfo, LocalDate dateOfRecord, Double height, Double weight, 
                       GrowthType type, StatusType status, Child child) {
        this.additionalInfo = additionalInfo;
        this.dateOfRecord = dateOfRecord;
        this.height = height;
        this.weight = weight;
        this.type = type;
        this.status = status;
        this.child = child;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    public LocalDate getDateOfRecord() {
        return dateOfRecord;
    }
    
    public void setDateOfRecord(LocalDate dateOfRecord) {
        this.dateOfRecord = dateOfRecord;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public GrowthType getType() {
        return type;
    }
    
    public void setType(GrowthType type) {
        this.type = type;
    }
    
    public StatusType getStatus() {
        return status;
    }
    
    public void setStatus(StatusType status) {
        this.status = status;
    }
    
    public Child getChild() {
        return child;
    }
    
    public void setChild(Child child) {
        this.child = child;
    }
    
    public enum GrowthType {
        EMOTIONAL, PHYSICAL, COGNITION
    }
    
    public enum StatusType {
        ACHIEVED, NOT_ACHIEVED
    }
}
