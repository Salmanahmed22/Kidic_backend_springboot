package com.example.kidic.repository;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.DiseaseAndAllergy;
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
class DiseaseAndAllergyRepositoryTest {

    @Autowired
    private DiseaseAndAllergyRepository diseaseAndAllergyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        diseaseAndAllergyRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByChild() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records for the child
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", "AI: Monitor symptoms", child); // Providing aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(disease1);

        // Test findByChild
        List<DiseaseAndAllergy> foundRecords = diseaseAndAllergyRepository.findByChild(child);
        assertEquals(2, foundRecords.size(), "Should find 2 disease/allergy records for the child");
        assertTrue(foundRecords.contains(allergy1), "Should contain the allergy record");
        assertTrue(foundRecords.contains(disease1), "Should contain the disease record");

        // Test with a different child (should return empty list)
        Child otherChild = new Child("Other Child", true, LocalDate.of(2019, 6, 20), parent, family);
        otherChild = entityManager.persistAndFlush(otherChild);
        List<DiseaseAndAllergy> emptyList = diseaseAndAllergyRepository.findByChild(otherChild);
        assertTrue(emptyList.isEmpty(), "Should return empty list for child with no records");
    }

    @Test
    void testFindByChildId() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records for the child
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", "AI: Monitor symptoms", child); // Providing aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(disease1);

        // Test findByChildId
        List<DiseaseAndAllergy> foundRecords = diseaseAndAllergyRepository.findByChildId(child.getId());
        assertEquals(2, foundRecords.size(), "Should find 2 disease/allergy records for the child ID");
        assertTrue(foundRecords.contains(allergy1), "Should contain the allergy record");
        assertTrue(foundRecords.contains(disease1), "Should contain the disease record");

        // Test with a non-existent child ID
        List<DiseaseAndAllergy> emptyList = diseaseAndAllergyRepository.findByChildId(999L);
        assertTrue(emptyList.isEmpty(), "Should return empty list for non-existent child ID");
    }

    @Test
    void testFindByType() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy allergy2 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Dust allergy", "AI: Use air purifier", child); // Providing aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", null, child); // Using null for aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(allergy2);
        entityManager.persistAndFlush(disease1);

        // Test findByType
        List<DiseaseAndAllergy> allergies = diseaseAndAllergyRepository.findByType(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY);
        assertEquals(2, allergies.size(), "Should find 2 allergy records");
        assertTrue(allergies.contains(allergy1), "Should contain the first allergy record");
        assertTrue(allergies.contains(allergy2), "Should contain the second allergy record");

        // Test with a different type
        List<DiseaseAndAllergy> diseases = diseaseAndAllergyRepository.findByType(DiseaseAndAllergy.DiseaseAllergyType.DISEASE);
        assertEquals(1, diseases.size(), "Should find 1 disease record");
        assertTrue(diseases.contains(disease1), "Should contain the disease record");
    }

    @Test
    void testFindByDescriptionContaining() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy allergy2 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Dust allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma attack", "AI: Use inhaler", child); // Providing aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(allergy2);
        entityManager.persistAndFlush(disease1);

        // Test findByDescriptionContaining
        List<DiseaseAndAllergy> foundRecords = diseaseAndAllergyRepository.findByDescriptionContaining("allergy");
        assertEquals(2, foundRecords.size(), "Should find 2 records containing 'allergy'");
        assertTrue(foundRecords.contains(allergy1), "Should contain the peanut allergy record");
        assertTrue(foundRecords.contains(allergy2), "Should contain the dust allergy record");

        // Test with no match
        List<DiseaseAndAllergy> noMatch = diseaseAndAllergyRepository.findByDescriptionContaining("xyz");
        assertTrue(noMatch.isEmpty(), "Should return empty list for no matching description");
    }

    @Test
    void testFindByAiResponseNotNull() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", "AI: Avoid peanuts", child); // Providing aiResponse
        DiseaseAndAllergy allergy2 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Dust allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", "AI: Use inhaler", child); // Providing aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(allergy2);
        entityManager.persistAndFlush(disease1);

        // Test findByAiResponseNotNull
        List<DiseaseAndAllergy> foundRecords = diseaseAndAllergyRepository.findByAiResponseNotNull();
        assertEquals(2, foundRecords.size(), "Should find 2 records with non-null AI response");
        assertTrue(foundRecords.contains(allergy1), "Should contain the peanut allergy record");
        assertTrue(foundRecords.contains(disease1), "Should contain the asthma record");
    }

    @Test
    void testCountByChild() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records for the child
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", null, child); // Using null for aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(disease1);

        // Test countByChild
        long count = diseaseAndAllergyRepository.countByChild(child);
        assertEquals(2, count, "Should count 2 records for the child");

        // Test with a different child
        Child otherChild = new Child("Other Child", true, LocalDate.of(2019, 6, 20), parent, family);
        otherChild = entityManager.persistAndFlush(otherChild);
        long otherCount = diseaseAndAllergyRepository.countByChild(otherChild);
        assertEquals(0, otherCount, "Should count 0 records for a child with no records");
    }

    @Test
    void testCountByChildAndType() {
        // Create and persist a parent
        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
        parent = entityManager.persistAndFlush(parent);

        // Create and persist a family
        Family family = new Family("securePassword123");
        family.setId(UUID.randomUUID());
        family.getParents().add(parent);
        family = entityManager.persistAndFlush(family);
        parent.setFamily(family);
        parent = entityManager.merge(parent);

        // Create and persist a child
        Child child = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), parent, family);
        child = entityManager.persistAndFlush(child);

        // Create and persist disease and allergy records for the child
        DiseaseAndAllergy allergy1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Peanut allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy allergy2 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY,
                "Dust allergy", null, child); // Using null for aiResponse
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.DISEASE,
                "Asthma", "AI: Use inhaler", child); // Providing aiResponse

        entityManager.persistAndFlush(allergy1);
        entityManager.persistAndFlush(allergy2);
        entityManager.persistAndFlush(disease1);

        // Test countByChildAndType
        long allergyCount = diseaseAndAllergyRepository.countByChildAndType(child, DiseaseAndAllergy.DiseaseAllergyType.ALLERGY);
        assertEquals(2, allergyCount, "Should count 2 allergy records for the child");

        long diseaseCount = diseaseAndAllergyRepository.countByChildAndType(child, DiseaseAndAllergy.DiseaseAllergyType.DISEASE);
        assertEquals(1, diseaseCount, "Should count 1 disease record for the child");

        // Test with a different type
        long otherCount = diseaseAndAllergyRepository.countByChildAndType(child, DiseaseAndAllergy.DiseaseAllergyType.CONDITION);
        assertEquals(0, otherCount, "Should count 0 records for a non-existent type");
    }
}