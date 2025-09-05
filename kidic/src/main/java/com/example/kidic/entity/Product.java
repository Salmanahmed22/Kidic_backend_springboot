package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String name;
    
    @NotBlank
    @Size(max = 500)
    private String link;
    
    @Size(max = 1000)
    private String description; // Note: keeping as "description" despite typo in ERD
    
    @Enumerated(EnumType.STRING)
    private ImageType image;
    
    @Enumerated(EnumType.STRING)
    private CategoryType category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AIInfo> aiInfos = new ArrayList<>();
    
    // Constructors
    public Product() {}
    
    public Product(String name, String link, String description, ImageType image, CategoryType category) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.image = image;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ImageType getImage() {
        return image;
    }
    
    public void setImage(ImageType image) {
        this.image = image;
    }
    
    public CategoryType getCategory() {
        return category;
    }
    
    public void setCategory(CategoryType category) {
        this.category = category;
    }
    
    public List<AIInfo> getAiInfos() {
        return aiInfos;
    }
    
    public void setAiInfos(List<AIInfo> aiInfos) {
        this.aiInfos = aiInfos;
    }
    
    public enum ImageType {
        IMAGE_1, IMAGE_2, IMAGE_3, IMAGE_4, IMAGE_5, CUSTOM
    }
    
    public enum CategoryType {
        TOYS, BOOKS, CLOTHING, FOOD, MEDICAL, EDUCATIONAL, ENTERTAINMENT, OTHER
    }
}
