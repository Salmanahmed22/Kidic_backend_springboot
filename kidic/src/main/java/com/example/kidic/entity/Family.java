package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "families")
public class Family {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Child> children = new ArrayList<>();

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Parent> parents = new ArrayList<>();

    // Constructors
    public Family() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    
    public List<Child> getChildren() {
        return children;
    }
    
    public void setChildren(List<Child> children) {
        this.children = children;
    }
    
    public List<Parent> getParents() {
        return parents;
    }
    
    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }

}
