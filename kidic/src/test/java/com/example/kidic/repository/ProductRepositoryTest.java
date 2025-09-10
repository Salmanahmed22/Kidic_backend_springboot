package com.example.kidic.repository;

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
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByNameContaining_Success() {
        // Positive case: Find products by name containing a substring
        Product product1 = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        Product product2 = new Product(
                "Red Toy Truck",
                "http://example.com/toy-truck",
                "A sturdy red toy truck",
                Product.ImageType.IMAGE_2,
                Product.CategoryType.TOYS
        );
        product1 = entityManager.persistAndFlush(product1);
        product2 = entityManager.persistAndFlush(product2);

        List<Product> products = productRepository.findByNameContaining("Toy");
        assertEquals(2, products.size(), "Two products should be found with 'Toy' in name");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Blue Toy Car")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Red Toy Truck")));
    }

    @Test
    void testFindByNameContaining_NonExistentName() {
        // Negative case: Find products with non-existent name substring
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        List<Product> products = productRepository.findByNameContaining("Book");
        assertTrue(products.isEmpty(), "No products should be found with 'Book' in name");
    }

    @Test
    void testFindByNameContaining_NullName() {
        // Negative case: Find products with null name
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.findByNameContaining(null);
        }, "findByNameContaining should throw IllegalArgumentException for null name");
    }

    @Test
    void testFindByCategory_Success() {
        // Positive case: Find products by category
        Product product1 = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        Product product2 = new Product(
                "Math Book",
                "http://example.com/math-book",
                "Educational math book for kids",
                Product.ImageType.IMAGE_2,
                Product.CategoryType.BOOKS
        );
        product1 = entityManager.persistAndFlush(product1);
        product2 = entityManager.persistAndFlush(product2);

        List<Product> toys = productRepository.findByCategory(Product.CategoryType.TOYS);
        assertEquals(1, toys.size(), "One product should be found in TOYS category");
        assertEquals("Blue Toy Car", toys.get(0).getName(), "Product name should match");
    }

    @Test
    void testFindByCategory_NonExistentCategory() {
        // Negative case: Find products with non-existent category
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        List<Product> products = productRepository.findByCategory(Product.CategoryType.FOOD);
        assertTrue(products.isEmpty(), "No products should be found in FOOD category");
    }

    @Test
    void testFindByCategory_NullCategory() {
        // Negative case: Find products with null category
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.findByCategory(null);
        }, "findByCategory should throw IllegalArgumentException for null category");
    }

    @Test
    void testFindByImage_Success() {
        // Positive case: Find products by image type
        Product product1 = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        Product product2 = new Product(
                "Math Book",
                "http://example.com/math-book",
                "Educational math book for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.BOOKS
        );
        product1 = entityManager.persistAndFlush(product1);
        product2 = entityManager.persistAndFlush(product2);

        List<Product> products = productRepository.findByImage(Product.ImageType.IMAGE_1);
        assertEquals(2, products.size(), "Two products should be found with IMAGE_1");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Blue Toy Car")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Math Book")));
    }

    @Test
    void testFindByImage_NonExistentImage() {
        // Negative case: Find products with non-existent image type
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        List<Product> products = productRepository.findByImage(Product.ImageType.IMAGE_5);
        assertTrue(products.isEmpty(), "No products should be found with IMAGE_5");
    }

    @Test
    void testFindByImage_NullImage() {
        // Negative case: Find products with null image type
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.findByImage(null);
        }, "findByImage should throw IllegalArgumentException for null image type");
    }

    @Test
    void testFindByDescriptionContaining_Success() {
        // Positive case: Find products by description containing a substring
        Product product1 = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        Product product2 = new Product(
                "Red Toy Truck",
                "http://example.com/toy-truck",
                "A sturdy red toy truck for kids",
                Product.ImageType.IMAGE_2,
                Product.CategoryType.TOYS
        );
        product1 = entityManager.persistAndFlush(product1);
        product2 = entityManager.persistAndFlush(product2);

        List<Product> products = productRepository.findByDescriptionContaining("for kids");
        assertEquals(2, products.size(), "Two products should be found with 'for kids' in description");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Blue Toy Car")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Red Toy Truck")));
    }

    @Test
    void testFindByDescriptionContaining_NonExistentDescription() {
        // Negative case: Find products with non-existent description substring
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        List<Product> products = productRepository.findByDescriptionContaining("for adults");
        assertTrue(products.isEmpty(), "No products should be found with 'for adults' in description");
    }

    @Test
    void testFindByDescriptionContaining_NullDescription() {
        // Negative case: Find products with null description
        Product product = new Product(
                "Blue Toy Car",
                "http://example.com/toy-car",
                "A fun blue toy car for kids",
                Product.ImageType.IMAGE_1,
                Product.CategoryType.TOYS
        );
        product = entityManager.persistAndFlush(product);

        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.findByDescriptionContaining(null);
        }, "findByDescriptionContaining should throw IllegalArgumentException for null description");
    }
}