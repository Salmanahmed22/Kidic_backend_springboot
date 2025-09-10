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
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private FamilyRepository familyRepository;

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
    void testFindByEmail() {
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parentRepository.save(parent);

        Optional<Parent> foundParent = parentRepository.findByEmail("john.smith@email.com");
        assertTrue(foundParent.isPresent());
        assertEquals("John Smith", foundParent.get().getName());
    }

    @Test
    void testFindByGender() {
        Parent parent1 = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent1.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        Parent parent2 = new Parent("Jane Smith", "1234567891", "jane.smith@email.com", false, "password");
        parent2.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parentRepository.saveAll(List.of(parent1, parent2));

        List<Parent> maleParents = parentRepository.findByGender(true);
        assertEquals(1, maleParents.size());
        assertEquals("John Smith", maleParents.get(0).getName());

        List<Parent> femaleParents = parentRepository.findByGender(false);
        assertEquals(1, femaleParents.size());
        assertEquals("Jane Smith", femaleParents.get(0).getName());
    }

    @Test
    void testFindByNameContaining() {
        Parent parent1 = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent1.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        Parent parent2 = new Parent("Jane Smith", "1234567891", "jane.smith@email.com", false, "password");
        parent2.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parentRepository.saveAll(List.of(parent1, parent2));

        List<Parent> parents = parentRepository.findByNameContaining("Smith");
        assertEquals(2, parents.size());
    }

    @Test
    void testFindByChildId() {
        // Create a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create a family (do not set ID manually)
        Family family = new Family("family123");
        family.getParents().add(parent); // Add parent to family
        family = entityManager.persistAndFlush(family);

        // Update parent's families collection
        parent.getFamilies().add(family);
        parent = entityManager.merge(parent); // Use merge instead of persist
        entityManager.flush();

        // Create a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Update parent's children collection
        parent.getChildren().add(child);
        parent = entityManager.merge(parent); // Use merge for updates
        entityManager.flush();

        // Debug entity state
        System.out.println("Parent ID: " + parent.getId() + ", Children: " + parent.getChildren().size());
        System.out.println("Child ID: " + child.getId() + ", Parent ID: " + child.getParent().getId());
        System.out.println("Family ID: " + family.getId() + ", Parents: " + family.getParents().size());

        // Clear persistence context
        entityManager.clear();

        // Test findByChildId
        List<Parent> parents = parentRepository.findByChildId(child.getId());
        assertEquals(1, parents.size());
        assertEquals("John Smith", parents.get(0).getName());
    }

    @Test
    void testExistsByEmail() {
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parentRepository.save(parent);

        assertTrue(parentRepository.existsByEmail("john.smith@email.com"));
        assertFalse(parentRepository.existsByEmail("nonexistent@email.com"));
    }
}