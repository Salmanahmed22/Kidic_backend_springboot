package com.example.kidic.repository;

import com.example.kidic.entity.AIInfo;
import com.example.kidic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIInfoRepository extends JpaRepository<AIInfo, Long> {
    
    List<AIInfo> findByProduct(Product product);
    
    @Query("SELECT ai FROM AIInfo ai WHERE ai.product.id = :productId")
    List<AIInfo> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT ai FROM AIInfo ai WHERE ai.placeOfUsage = :placeOfUsage")
    List<AIInfo> findByPlaceOfUsage(@Param("placeOfUsage") AIInfo.PlaceOfUsageType placeOfUsage);
    
    @Query("SELECT ai FROM AIInfo ai WHERE ai.gender = :gender")
    List<AIInfo> findByGender(@Param("gender") Boolean gender);
    
    @Query("SELECT ai FROM AIInfo ai WHERE ai.age = :age")
    List<AIInfo> findByAge(@Param("age") Integer age);
    
    @Query("SELECT ai FROM AIInfo ai WHERE ai.age BETWEEN :minAge AND :maxAge")
    List<AIInfo> findByAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    long countByProduct(Product product);
}
