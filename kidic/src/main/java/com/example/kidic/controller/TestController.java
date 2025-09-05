package com.example.kidic.controller;

import com.example.kidic.entity.Parent;
import com.example.kidic.entity.Family;
import com.example.kidic.entity.Child;
import com.example.kidic.repository.ParentRepository;
import com.example.kidic.repository.FamilyRepository;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.config.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private ParentRepository parentRepository;
    
    @Autowired
    private FamilyRepository familyRepository;
    
    @Autowired
    private ChildRepository childRepository;
    
    @Autowired
    private DataInitializer dataInitializer;
    
    @GetMapping("/database")
    public ResponseEntity<String> testDatabaseConnection() {
        try {
            long parentCount = parentRepository.count();
            long familyCount = familyRepository.count();
            long childCount = childRepository.count();
            return ResponseEntity.ok("Database connection successful! Parents: " + parentCount + 
                                   ", Families: " + familyCount + ", Children: " + childCount);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/parents")
    public ResponseEntity<List<Parent>> getAllParents() {
        try {
            List<Parent> parents = parentRepository.findAll();
            return ResponseEntity.ok(parents);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/families")
    public ResponseEntity<List<Family>> getAllFamilies() {
        try {
            List<Family> families = familyRepository.findAll();
            return ResponseEntity.ok(families);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/children")
    public ResponseEntity<List<Child>> getAllChildren() {
        try {
            List<Child> children = childRepository.findAll();
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @PostMapping("/parents")
    public ResponseEntity<Parent> createParent(@RequestBody Parent parent) {
        try {
            Parent savedParent = parentRepository.save(parent);
            return ResponseEntity.ok(savedParent);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @PostMapping("/init-data")
    public ResponseEntity<String> initializeData() {
        try {
            dataInitializer.run();
            return ResponseEntity.ok("Data initialization triggered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/clear-data")
    public ResponseEntity<String> clearData() {
        try {
            // Clear all data
            childRepository.deleteAll();
            parentRepository.deleteAll();
            familyRepository.deleteAll();
            return ResponseEntity.ok("All data cleared successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/test-parent")
    public ResponseEntity<String> createTestParent() {
        try {
            Parent testParent = new Parent("Test Parent", "1234567890", "test@email.com", true, "password123");
            testParent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
            parentRepository.save(testParent);
            return ResponseEntity.ok("Test parent created successfully! ID: " + testParent.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating test parent: " + e.getMessage());
        }
    }
}
