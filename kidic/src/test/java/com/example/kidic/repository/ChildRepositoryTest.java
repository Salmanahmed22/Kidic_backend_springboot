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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ChildRepositoryTest {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentRepository parentRepository;

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
    void testFindByParent() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);

        // Update parent's families collection
        parent.getFamilies().add(family);
        parent = entityManager.merge(parent);
        entityManager.flush();

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Update parent's children collection
        parent.getChildren().add(child);
        parent = entityManager.merge(parent);
        entityManager.flush();

        // Test findByParent
        List<Child> children = childRepository.findByParent(parent);
        assertEquals(1, children.size());
        assertEquals("Emma Smith", children.get(0).getName());
    }

    @Test
    void testFindByFamily() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);

        // Update parent's families collection
        parent.getFamilies().add(family);
        parent = entityManager.merge(parent);
        entityManager.flush();

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Test findByFamily
        List<Child> children = childRepository.findByFamily(family);
        assertEquals(1, children.size());
        assertEquals("Emma Smith", children.get(0).getName());
    }

    @Test
    void testFindByNameContaining() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), parent, family);
        child1 = entityManager.persistAndFlush(child1);
        child2 = entityManager.persistAndFlush(child2);

        // Test findByNameContaining
        List<Child> children = childRepository.findByNameContaining("Smith");
        assertEquals(2, children.size());
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("Emma Smith")));
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("Liam Smith")));
    }

    @Test
    void testFindByGender() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), parent, family);
        child1 = entityManager.persistAndFlush(child1);
        child2 = entityManager.persistAndFlush(child2);

        // Test findByGender
        List<Child> femaleChildren = childRepository.findByGender(false);
        assertEquals(1, femaleChildren.size());
        assertEquals("Emma Smith", femaleChildren.get(0).getName());

        List<Child> maleChildren = childRepository.findByGender(true);
        assertEquals(1, maleChildren.size());
        assertEquals("Liam Smith", maleChildren.get(0).getName());
    }

    @Test
    void testFindByBirthDateBetween() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), parent, family);
        child1 = entityManager.persistAndFlush(child1);
        child2 = entityManager.persistAndFlush(child2);

        // Test findByBirthDateBetween
        List<Child> children = childRepository.findByBirthDateBetween(
                LocalDate.of(2019, 1, 1),
                LocalDate.of(2020, 12, 31)
        );
        assertEquals(2, children.size());
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("Emma Smith")));
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("Liam Smith")));
    }

    @Test
    void testFindByParentId() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Test findByParentId
        List<Child> children = childRepository.findByParentId(parent.getId());
        assertEquals(1, children.size());
        assertEquals("Emma Smith", children.get(0).getName());
    }

    @Test
    void testFindByFamilyId() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Test findByFamilyId
        List<Child> children = childRepository.findByFamilyId(family.getId());
        assertEquals(1, children.size());
        assertEquals("Emma Smith", children.get(0).getName());
    }

    @Test
    void testCountByParent() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), parent, family);
        child1 = entityManager.persistAndFlush(child1);
        child2 = entityManager.persistAndFlush(child2);

        // Test countByParent
        long count = childRepository.countByParent(parent);
        assertEquals(2, count);
    }

    @Test
    void testCountByFamily() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("family123");
        family = entityManager.persistAndFlush(family);

        // Create and persist children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), parent, family);
        child1 = entityManager.persistAndFlush(child1);
        child2 = entityManager.persistAndFlush(child2);

        // Test countByFamily
        long count = childRepository.countByFamily(family);
        assertEquals(2, count);
    }
}