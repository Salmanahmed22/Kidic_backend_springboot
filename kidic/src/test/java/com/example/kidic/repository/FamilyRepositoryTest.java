package com.example.kidic.repository;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.Family;
import com.example.kidic.entity.Parent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FamilyRepositoryTest {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        childRepository.deleteAll();
        familyRepository.deleteAll();
        parentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByPassword() {
        // Create and persist a family
        Family family = new Family("securePassword123");
        family = entityManager.persistAndFlush(family);

        // Test findByPassword
        Optional<Family> foundFamily = familyRepository.findByPassword("securePassword123");
        assertTrue(foundFamily.isPresent());
        assertEquals(family.getId(), foundFamily.get().getId());

        // Test non-existent password
        Optional<Family> notFound = familyRepository.findByPassword("wrongPassword");
        assertFalse(notFound.isPresent());
    }

    @Test
    void testFindByParentId() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);

        // Update parent's families collection
        parent.getFamilies().add(family);
        parent = entityManager.merge(parent);
        entityManager.flush();

        // Test findByParentId
        List<Family> families = familyRepository.findByParentId(parent.getId());
        assertEquals(1, families.size());
        assertEquals(family.getId(), families.get(0).getId());

        // Test non-existent parent ID
        List<Family> noFamilies = familyRepository.findByParentId(999L);
        assertTrue(noFamilies.isEmpty());
    }

    @Test
    void testFindByChildId() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family = entityManager.persistAndFlush(family);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Update family's children collection
        family.getChildren().add(child);
        family = entityManager.merge(family);
        entityManager.flush();

        // Update parent's children collection
        parent.getChildren().add(child);
        parent = entityManager.merge(parent);
        entityManager.flush();

        // Debug entity state
        System.out.println("Family ID: " + family.getId() + ", Children: " + family.getChildren().size());
        System.out.println("Child ID: " + child.getId() + ", Family ID: " + child.getFamily().getId());
        System.out.println("Parent ID: " + parent.getId() + ", Children: " + parent.getChildren().size());

        // Test findByChildId
        Optional<Family> foundFamily = familyRepository.findByChildId(child.getId());
        assertTrue(foundFamily.isPresent());
        assertEquals(family.getId(), foundFamily.get().getId());

        // Test non-existent child ID
        Optional<Family> notFound = familyRepository.findByChildId(999L);
        assertFalse(notFound.isPresent());
    }
}