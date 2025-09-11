package com.example.kidic.repository;

import com.example.kidic.entity.EducationalContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EducationalContentRepositoryTest {

    @Autowired
    private EducationalContentRepository educationalContentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        educationalContentRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByTitleContaining() {
        // Create and persist educational content records
        EducationalContent content1 = new EducationalContent(
                "Learn Math Basics",
                "https://example.com/math1",
                "30 mins",
                "Basic math concepts for kids"
        );
        EducationalContent content2 = new EducationalContent(
                "Advanced Math",
                "https://example.com/math2",
                "60 mins",
                "Advanced math techniques"
        );
        EducationalContent content3 = new EducationalContent(
                "Math Games",
                "https://example.com/math3",
                "45 mins",
                "Fun math games for children"
        );

        entityManager.persistAndFlush(content1);
        entityManager.persistAndFlush(content2);
        entityManager.persistAndFlush(content3);

        // Test findByTitleContaining with partial match
        List<EducationalContent> foundRecords = educationalContentRepository.findByTitleContaining("Math");
        assertEquals(3, foundRecords.size(), "Should find 3 records containing 'Math'");
        assertTrue(foundRecords.contains(content1), "Should contain 'Learn Math Basics'");
        assertTrue(foundRecords.contains(content2), "Should contain 'Advanced Math'");
        assertTrue(foundRecords.contains(content3), "Should contain 'Math Games'");

        // Test with no match
        List<EducationalContent> noMatch = educationalContentRepository.findByTitleContaining("Science");
        assertTrue(noMatch.isEmpty(), "Should return empty list for no matching title");
    }

    @Test
    void testFindByEstimatedTime() {
        // Create and persist educational content records
        EducationalContent content1 = new EducationalContent(
                "Learn Math Basics",
                "https://example.com/math1",
                "30 mins",
                "Basic math concepts for kids"
        );
        EducationalContent content2 = new EducationalContent(
                "Reading Skills",
                "https://example.com/reading",
                "30 mins",
                "Reading techniques for beginners"
        );
        EducationalContent content3 = new EducationalContent(
                "Advanced Math",
                "https://example.com/math2",
                "60 mins",
                "Advanced math techniques"
        );

        entityManager.persistAndFlush(content1);
        entityManager.persistAndFlush(content2);
        entityManager.persistAndFlush(content3);

        // Test findByEstimatedTime with exact match
        List<EducationalContent> foundRecords = educationalContentRepository.findByEstimatedTime("30 mins");
        assertEquals(2, foundRecords.size(), "Should find 2 records with '30 mins' estimated time");
        assertTrue(foundRecords.contains(content1), "Should contain 'Learn Math Basics'");
        assertTrue(foundRecords.contains(content2), "Should contain 'Reading Skills'");

        // Test with no match
        List<EducationalContent> noMatch = educationalContentRepository.findByEstimatedTime("15 mins");
        assertTrue(noMatch.isEmpty(), "Should return empty list for no matching estimated time");
    }

    @Test
    void testFindByDescriptionContaining() {
        // Create and persist educational content records
        EducationalContent content1 = new EducationalContent(
                "Learn Math Basics",
                "https://example.com/math1",
                "30 mins",
                "Basic math concepts for kids"
        );
        EducationalContent content2 = new EducationalContent(
                "Reading Skills",
                "https://example.com/reading",
                "30 mins",
                "Reading techniques for beginners"
        );
        EducationalContent content3 = new EducationalContent(
                "Math Games",
                "https://example.com/math3",
                "45 mins",
                "Fun math games for children"
        );

        entityManager.persistAndFlush(content1);
        entityManager.persistAndFlush(content2);
        entityManager.persistAndFlush(content3);

        // Test findByDescriptionContaining with partial match
        List<EducationalContent> foundRecords = educationalContentRepository.findByDescriptionContaining("math");
        assertEquals(2, foundRecords.size(), "Should find 2 records containing 'math'");
        assertTrue(foundRecords.contains(content1), "Should contain 'Learn Math Basics'");
        assertTrue(foundRecords.contains(content3), "Should contain 'Math Games'");

        // Test with no match
        List<EducationalContent> noMatch = educationalContentRepository.findByDescriptionContaining("science");
        assertTrue(noMatch.isEmpty(), "Should return empty list for no matching description");
    }
}