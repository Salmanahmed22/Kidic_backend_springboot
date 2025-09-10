package com.example.kidic.repository;

import com.example.kidic.entity.AIInfo;
import com.example.kidic.entity.Product;
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
class AIInfoRepositoryTest {

    @Autowired
    private AIInfoRepository aiInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        aiInfoRepository.deleteAll();
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByProduct_Success() {
        // Positive case: Find AIInfo by Product
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.PLAYGROUND, false, 6, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        // Update product's aiInfos collection
        product.getAiInfos().add(aiInfo1);
        product.getAiInfos().add(aiInfo2);
        product = entityManager.merge(product);
        entityManager.flush();

        List<AIInfo> aiInfos = aiInfoRepository.findByProduct(product);
        assertEquals(2, aiInfos.size(), "Two AIInfo records should be found for the product");
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 5));
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 6));
    }

    @Test
    void testFindByProduct_NonExistentProduct() {
        // Negative case: Find AIInfo with non-existent Product
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        Product nonExistentProduct = new Product();
        nonExistentProduct.setId(999L); // Non-persisted product

        List<AIInfo> aiInfos = aiInfoRepository.findByProduct(nonExistentProduct);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for non-existent product");
    }

    @Test
    void testFindByProductId_Success() {
        // Positive case: Find AIInfo by product ID
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        aiInfo = entityManager.persistAndFlush(aiInfo);

        List<AIInfo> aiInfos = aiInfoRepository.findByProductId(product.getId());
        assertEquals(1, aiInfos.size(), "One AIInfo record should be found for the product ID");
        assertEquals(5, aiInfos.get(0).getAge(), "AIInfo age should match");
    }

    @Test
    void testFindByProductId_NonExistentProductId() {
        // Negative case: Find AIInfo with non-existent product ID
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        List<AIInfo> aiInfos = aiInfoRepository.findByProductId(999L);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for non-existent product ID");
    }

    @Test
    void testFindByPlaceOfUsage_Success() {
        // Positive case: Find AIInfo by place of usage
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, false, 6, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        List<AIInfo> aiInfos = aiInfoRepository.findByPlaceOfUsage(AIInfo.PlaceOfUsageType.HOME);
        assertEquals(2, aiInfos.size(), "Two AIInfo records should be found for HOME");
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 5));
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 6));
    }

    @Test
    void testFindByPlaceOfUsage_NonExistentPlace() {
        // Negative case: Find AIInfo with non-existent place of usage
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        aiInfo = entityManager.persistAndFlush(aiInfo);

        List<AIInfo> aiInfos = aiInfoRepository.findByPlaceOfUsage(AIInfo.PlaceOfUsageType.SCHOOL);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for SCHOOL");
    }

    @Test
    void testFindByPlaceOfUsage_NullPlace() {
        // Negative case: Find AIInfo with null place of usage
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            aiInfoRepository.findByPlaceOfUsage(null);
        }, "findByPlaceOfUsage should throw IllegalArgumentException for null place of usage");
    }

    @Test
    void testFindByGender_Success() {
        // Positive case: Find AIInfo by gender
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.PLAYGROUND, true, 6, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        List<AIInfo> aiInfos = aiInfoRepository.findByGender(true);
        assertEquals(2, aiInfos.size(), "Two AIInfo records should be found for male gender");
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 5));
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 6));
    }

    @Test
    void testFindByGender_NonExistentGender() {
        // Negative case: Find AIInfo with non-existent gender
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        aiInfo = entityManager.persistAndFlush(aiInfo);

        List<AIInfo> aiInfos = aiInfoRepository.findByGender(false);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for female gender");
    }

    @Test
    void testFindByGender_NullGender() {
        // Negative case: Find AIInfo with null gender
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            aiInfoRepository.findByGender(null);
        }, "findByGender should throw IllegalArgumentException for null gender");
    }

    @Test
    void testFindByAge_Success() {
        // Positive case: Find AIInfo by age
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.PLAYGROUND, false, 5, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        List<AIInfo> aiInfos = aiInfoRepository.findByAge(5);
        assertEquals(2, aiInfos.size(), "Two AIInfo records should be found for age 5");
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getPlaceOfUsage() == AIInfo.PlaceOfUsageType.HOME));
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getPlaceOfUsage() == AIInfo.PlaceOfUsageType.PLAYGROUND));
    }

    @Test
    void testFindByAge_NonExistentAge() {
        // Negative case: Find AIInfo with non-existent age
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        aiInfo = entityManager.persistAndFlush(aiInfo);

        List<AIInfo> aiInfos = aiInfoRepository.findByAge(10);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for age 10");
    }

    @Test
    void testFindByAge_NullAge() {
        // Negative case: Find AIInfo with null age
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            aiInfoRepository.findByAge(null);
        }, "findByAge should throw IllegalArgumentException for null age");
    }

    @Test
    void testFindByAgeBetween_Success() {
        // Positive case: Find AIInfo by age range
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.PLAYGROUND, false, 7, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        List<AIInfo> aiInfos = aiInfoRepository.findByAgeBetween(4, 8);
        assertEquals(2, aiInfos.size(), "Two AIInfo records should be found for age range 4-8");
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 5));
        assertTrue(aiInfos.stream().anyMatch(ai -> ai.getAge() == 7));
    }

    @Test
    void testFindByAgeBetween_NonExistentAgeRange() {
        // Negative case: Find AIInfo with non-existent age range
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        aiInfo = entityManager.persistAndFlush(aiInfo);

        List<AIInfo> aiInfos = aiInfoRepository.findByAgeBetween(10, 15);
        assertTrue(aiInfos.isEmpty(), "No AIInfo records should be found for age range 10-15");
    }

    @Test
    void testFindByAgeBetween_NullAgeRange() {
        // Negative case: Find AIInfo with null age range
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            aiInfoRepository.findByAgeBetween(null, 10);
        }, "findByAgeBetween should throw IllegalArgumentException for null minAge");

        assertThrows(IllegalArgumentException.class, () -> {
            aiInfoRepository.findByAgeBetween(5, null);
        }, "findByAgeBetween should throw IllegalArgumentException for null maxAge");
    }

    @Test
    void testCountByProduct_Success() {
        // Positive case: Count AIInfo by Product
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 5, product);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.PLAYGROUND, false, 6, product);
        aiInfo1 = entityManager.persistAndFlush(aiInfo1);
        aiInfo2 = entityManager.persistAndFlush(aiInfo2);

        long count = aiInfoRepository.countByProduct(product);
        assertEquals(2, count, "Two AIInfo records should be counted for the product");
    }

    @Test
    void testCountByProduct_NonExistentProduct() {
        // Negative case: Count AIInfo with non-existent Product
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        Product nonExistentProduct = new Product();
        nonExistentProduct.setId(999L); // Non-persisted product

        long count = aiInfoRepository.countByProduct(nonExistentProduct);
        assertEquals(0, count, "No AIInfo records should be counted for non-existent product");
    }
}