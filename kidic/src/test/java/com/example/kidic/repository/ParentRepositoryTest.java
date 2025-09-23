package com.example.kidic.repository;

import com.example.kidic.entity.Parent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        parentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByEmail_Success() {
        // Positive case: Find parent by existing email
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent);

        Optional<Parent> foundParent = parentRepository.findByEmail("john.smith@email.com");
        assertTrue(foundParent.isPresent(), "Parent should be found by email");
        assertEquals("John Smith", foundParent.get().getName(), "Name should match");
    }

    @Test
    void testFindByEmail_NonExistentEmail() {
        // Negative case: Find parent with non-existent email
        Optional<Parent> foundParent = parentRepository.findByEmail("nonexistent@email.com");
        assertFalse(foundParent.isPresent(), "No parent should be found for non-existent email");
    }

    @Test
    void testFindByGender_Success() {
        // Positive case: Find parents by gender
        Parent parent1 = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent1.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        Parent parent2 = new Parent("Jane Smith", "0987654321", "jane.smith@email.com", false, "password");
        parent2.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent1);
        entityManager.persistAndFlush(parent2);

        List<Parent> maleParents = parentRepository.findByGender(true);
        assertEquals(1, maleParents.size(), "Should find one male parent");
        assertEquals("John Smith", maleParents.get(0).getName(), "Should be John Smith");

        List<Parent> femaleParents = parentRepository.findByGender(false);
        assertEquals(1, femaleParents.size(), "Should find one female parent");
        assertEquals("Jane Smith", femaleParents.get(0).getName(), "Should be Jane Smith");
    }

    @Test
    void testFindByGender_NoMatch() {
        // Negative case: Find parents with no matching gender
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent);

        List<Parent> femaleParents = parentRepository.findByGender(false);
        assertTrue(femaleParents.isEmpty(), "Should find no female parents");
    }

    @Test
    void testFindByNameContaining_Success() {
        // Positive case: Find parents by name containing
        Parent parent1 = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent1.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        Parent parent2 = new Parent("Jane Smithson", "0987654321", "jane.smith@email.com", false, "password");
        parent2.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent1);
        entityManager.persistAndFlush(parent2);

        List<Parent> parents = parentRepository.findByNameContaining("Smith");
        assertEquals(2, parents.size(), "Should find two parents with 'Smith' in name");
        assertTrue(parents.contains(parent1), "Should contain John Smith");
        assertTrue(parents.contains(parent2), "Should contain Jane Smithson");
    }

    @Test
    void testFindByNameContaining_NoMatch() {
        // Negative case: Find parents with no matching name
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent);

        List<Parent> parents = parentRepository.findByNameContaining("Jones");
        assertTrue(parents.isEmpty(), "Should find no parents with 'Jones' in name");
    }

    @Test
    void testExistsByEmail_Success() {
        // Positive case: Check if email exists
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        entityManager.persistAndFlush(parent);

        boolean exists = parentRepository.existsByEmail("john.smith@email.com");
        assertTrue(exists, "Email should exist");
    }

    @Test
    void testExistsByEmail_NonExistentEmail() {
        // Negative case: Check if non-existent email exists
        boolean exists = parentRepository.existsByEmail("nonexistent@email.com");
        assertFalse(exists, "Non-existent email should not exist");
    }
}