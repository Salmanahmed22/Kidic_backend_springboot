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
import java.util.UUID;

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
    void testFindByParentId_Success() {
        // Positive case: Find families by parent ID
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT); // Fixed method name
        parent.setFamily(family);
        parent = entityManager.persistAndFlush(parent);

        // Update family's parents collection
        family.getParents().add(parent);
        family = entityManager.merge(family);
        entityManager.flush();

        List<Family> families = familyRepository.findByParentId(parent.getId());
        assertEquals(1, families.size(), "One family should be found for the parent ID");
        assertEquals(family.getId(), families.get(0).getId(), "Family ID should match");
    }

    @Test
    void testFindByParentId_NonExistentParentId() {
        // Negative case: Find families with non-existent parent ID
        List<Family> families = familyRepository.findByParentId(999L);
        assertTrue(families.isEmpty(), "No families should be found for non-existent parent ID");
    }

    @Test
    void testFindByParentId_UnassociatedParent() {
        // Negative case: Find families with unassociated parent ID
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT); // Fixed method name
        parent = entityManager.persistAndFlush(parent);

        List<Family> families = familyRepository.findByParentId(parent.getId());
        assertTrue(families.isEmpty(), "No families should be found for unassociated parent");
    }

    @Test
    void testFindByChildId_Success() {
        // Positive case: Find family by child ID
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family); // Adjusted date to past
        child = entityManager.persistAndFlush(child);

        // Update family's children collection
        family.getChildren().add(child);
        family = entityManager.merge(family);
        entityManager.flush();

        Optional<Family> foundFamily = familyRepository.findByChildId(child.getId());
        assertTrue(foundFamily.isPresent(), "Family should be found by child ID");
        assertEquals(family.getId(), foundFamily.get().getId(), "Family ID should match");
    }

    @Test
    void testFindByChildId_NonExistentChildId() {
        // Negative case: Find family with non-existent child ID
        Optional<Family> foundFamily = familyRepository.findByChildId(999L);
        assertFalse(foundFamily.isPresent(), "No family should be found for non-existent child ID");
    }

    @Test
    void testFindByChildId_UnassociatedChild() {
        // Negative case: Find family with unassociated child ID
        Child child = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, null); // Adjusted date to past
        child = entityManager.persistAndFlush(child);

        Optional<Family> foundFamily = familyRepository.findByChildId(child.getId());
        assertFalse(foundFamily.isPresent(), "No family should be found for unassociated child");
    }

    @Test
    void testFindByParentId_EmptyFamily() {
        // Additional case: Find families with no parents
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        List<Family> families = familyRepository.findByParentId(999L); // Non-existent parent ID
        assertTrue(families.isEmpty(), "No families should be found with no parents");
    }

    @Test
    void testFindByChildId_EmptyFamily() {
        // Additional case: Find family with no children
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Optional<Family> foundFamily = familyRepository.findByChildId(999L); // Non-existent child ID
        assertFalse(foundFamily.isPresent(), "No family should be found with no children");
    }
}