package com.example.kidic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "image_type")
    private ImageType imageType;
    
    @Size(max = 500)
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_content", columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] imageContent;
    
    @Column(name = "image_size")
    private Long imageSize;
    
    @Size(max = 100)
    @Column(name = "image_content_type")
    private String imageContentType;
    
    @Enumerated(EnumType.STRING)
    private CategoryType category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-aiInfos")
    private List<AIInfo> aiInfos = new ArrayList<>();
    
    // Constructors
    public Product() {}
    
    public Product(String name, String link, String description, ImageType imageType, CategoryType category) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.imageType = imageType;
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
    
    public ImageType getImageType() {
        return imageType;
    }
    
    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public byte[] getImageContent() {
        return imageContent;
    }
    
    public void setImageContent(byte[] imageContent) {
        this.imageContent = imageContent;
    }
    
    public Long getImageSize() {
        return imageSize;
    }
    
    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }
    
    public String getImageContentType() {
        return imageContentType;
    }
    
    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
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
