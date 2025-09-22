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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ChildRepositoryTest {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private ParentRepository parentRepository;

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
    void testFindByFamily_Success() {
        // Positive case: Find children by family
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        // Update family's children collection
        family.getChildren().add(child1);
        family.getChildren().add(child2);
        family = entityManager.merge(family);
        entityManager.flush();

        List<Child> children = childRepository.findByFamily(family);
        assertEquals(2, children.size(), "Should find two children for the family");
        assertTrue(children.contains(child1), "Should contain Emma Smith");
        assertTrue(children.contains(child2), "Should contain Liam Smith");
    }

    @Test
    void testFindByFamily_EmptyFamily() {
        // Negative case: Find children with empty family
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        List<Child> children = childRepository.findByFamily(family);
        assertTrue(children.isEmpty(), "Should find no children for empty family");
    }

    @Test
    void testFindByFamily_NonExistentFamily() {
        // Negative case: Find children with non-existent family
        Family family = new Family();
        family.setId(UUID.randomUUID()); // Non-persisted family
        List<Child> children = childRepository.findByFamily(family);
        assertTrue(children.isEmpty(), "Should find no children for non-existent family");
    }

    @Test
    void testFindByNameContaining_Success() {
        // Positive case: Find children by name containing
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        List<Child> children = childRepository.findByNameContaining("Smith");
        assertEquals(2, children.size(), "Should find two children with 'Smith' in name");
        assertTrue(children.contains(child1), "Should contain Emma Smith");
        assertTrue(children.contains(child2), "Should contain Liam Smith");
    }

    @Test
    void testFindByNameContaining_NoMatch() {
        // Negative case: Find children with no matching name
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        entityManager.persistAndFlush(child);

        List<Child> children = childRepository.findByNameContaining("Jones");
        assertTrue(children.isEmpty(), "Should find no children with 'Jones' in name");
    }

    @Test
    void testFindByGender_Success() {
        // Positive case: Find children by gender
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        List<Child> maleChildren = childRepository.findByGender(true);
        assertEquals(1, maleChildren.size(), "Should find one male child");
        assertEquals(child2, maleChildren.get(0), "Should be Liam Smith");

        List<Child> femaleChildren = childRepository.findByGender(false);
        assertEquals(1, femaleChildren.size(), "Should find one female child");
        assertEquals(child1, femaleChildren.get(0), "Should be Emma Smith");
    }

    @Test
    void testFindByGender_NoMatch() {
        // Negative case: Find children with no matching gender
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        entityManager.persistAndFlush(child);

        List<Child> maleChildren = childRepository.findByGender(true);
        assertTrue(maleChildren.isEmpty(), "Should find no male children");
    }

    @Test
    void testFindByBirthDateBetween_Success() {
        // Positive case: Find children by birth date range
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        List<Child> children = childRepository.findByBirthDateBetween(LocalDate.of(2015, 1, 1), LocalDate.of(2016, 12, 31));
        assertEquals(1, children.size(), "Should find one child in date range");
        assertEquals(child1, children.get(0), "Should be Emma Smith");
    }

    @Test
    void testFindByBirthDateBetween_NoMatch() {
        // Negative case: Find children with no matching birth date
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        entityManager.persistAndFlush(child);

        List<Child> children = childRepository.findByBirthDateBetween(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31));
        assertTrue(children.isEmpty(), "Should find no children in date range");
    }

    @Test
    void testFindByFamilyId_Success() {
        // Positive case: Find children by family ID
        Family family = new Family();
        family = entityManager.persistAndFlush(family);
        UUID familyId = family.getId();

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        // Update family's children collection
        family.getChildren().add(child1);
        family.getChildren().add(child2);
        family = entityManager.merge(family);
        entityManager.flush();

        List<Child> children = childRepository.findByFamilyId(familyId);
        assertEquals(2, children.size(), "Should find two children for the family ID");
        assertTrue(children.contains(child1), "Should contain Emma Smith");
        assertTrue(children.contains(child2), "Should contain Liam Smith");
    }

    @Test
    void testFindByFamilyId_NonExistentFamilyId() {
        // Negative case: Find children with non-existent family ID
        UUID nonExistentFamilyId = UUID.randomUUID();
        List<Child> children = childRepository.findByFamilyId(nonExistentFamilyId);
        assertTrue(children.isEmpty(), "Should find no children for non-existent family ID");
    }

    @Test
    void testCountByFamily_Success() {
        // Positive case: Count children by family
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        Child child1 = new Child("Emma Smith", false, LocalDate.of(2015, 5, 15), null, family);
        Child child2 = new Child("Liam Smith", true, LocalDate.of(2018, 3, 10), null, family);
        entityManager.persistAndFlush(child1);
        entityManager.persistAndFlush(child2);

        // Update family's children collection
        family.getChildren().add(child1);
        family.getChildren().add(child2);
        family = entityManager.merge(family);
        entityManager.flush();

        long count = childRepository.countByFamily(family);
        assertEquals(2, count, "Should count two children in the family");
    }

    @Test
    void testCountByFamily_EmptyFamily() {
        // Negative case: Count children in empty family
        Family family = new Family();
        family = entityManager.persistAndFlush(family);

        long count = childRepository.countByFamily(family);
        assertEquals(0, count, "Should count zero children in empty family");
    }
}