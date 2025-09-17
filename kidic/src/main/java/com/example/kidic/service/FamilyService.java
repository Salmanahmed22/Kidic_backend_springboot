package com.example.kidic.service;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.Family;
import com.example.kidic.entity.Parent;
import com.example.kidic.repository.FamilyRepository;
import com.example.kidic.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FamilyService {
    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private ParentRepository parentRepository;

    public Family createFamily() {
        System.out.println("new family got created");
        Family family = new Family();
        familyRepository.save(family);
        return family;
    }
    public void addParentToFamily(Family family, Parent parent) {
        if (family.getParents().stream()
                .anyMatch(p -> p.getId().equals(parent.getId()))) {
            throw new IllegalArgumentException("Parent already exists");
        }

        family.getParents().add(parent);
        parent.setFamily(family);

        parentRepository.save(parent);
        familyRepository.save(family);
        System.out.println("addParentToFamily");
    }


    public Family getFamilyById(UUID familyId) {
        return familyRepository.findById(familyId).orElse(null);
    }

    public void deleteChild(UUID familyId, Child child) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));
        family.getChildren().remove(child);
        familyRepository.save(family);
    }

    public boolean isChildMember(UUID familyId, Child child) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));
        return family.getChildren().contains(child);
    }
}
