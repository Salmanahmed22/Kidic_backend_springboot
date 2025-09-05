package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ai_infos")
public class AIInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "place_of_usage")
    private PlaceOfUsageType placeOfUsage;
    
    private Boolean gender; // true for male, false for female
    
    private Integer age;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    // Constructors
    public AIInfo() {}
    
    public AIInfo(PlaceOfUsageType placeOfUsage, Boolean gender, Integer age, Product product) {
        this.placeOfUsage = placeOfUsage;
        this.gender = gender;
        this.age = age;
        this.product = product;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public PlaceOfUsageType getPlaceOfUsage() {
        return placeOfUsage;
    }
    
    public void setPlaceOfUsage(PlaceOfUsageType placeOfUsage) {
        this.placeOfUsage = placeOfUsage;
    }
    
    public Boolean getGender() {
        return gender;
    }
    
    public void setGender(Boolean gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public enum PlaceOfUsageType {
        HOME, SCHOOL, HOSPITAL, OUTDOOR, INDOOR, PLAYGROUND, OTHER
    }
}
