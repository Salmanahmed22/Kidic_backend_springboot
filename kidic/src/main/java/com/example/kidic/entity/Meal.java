package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
public class Meal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @ElementCollection
    @CollectionTable(name = "meal_ingredients", joinColumns = @JoinColumn(name = "meal_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();
    
    @Size(max = 2000)
    private String recipe;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;
    
    // Constructors
    public Meal() {}
    
    public Meal(String title, List<String> ingredients, String recipe) {
        this.title = title;
        this.ingredients = ingredients;
        this.recipe = recipe;
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
    
    public List<String> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getRecipe() {
        return recipe;
    }
    
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }
    
    public Child getChild() {
        return child;
    }
    
    public void setChild(Child child) {
        this.child = child;
    }
}
