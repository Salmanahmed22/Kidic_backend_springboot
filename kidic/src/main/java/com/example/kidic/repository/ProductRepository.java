package com.example.kidic.repository;

import com.example.kidic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") Product.CategoryType category);
    
    @Query("SELECT p FROM Product p WHERE p.imageType = :imageType")
    List<Product> findByImage(@Param("imageType") Product.ImageType image);
    
    @Query("SELECT p FROM Product p WHERE p.description LIKE %:description%")
    List<Product> findByDescriptionContaining(@Param("description") String description);
}
