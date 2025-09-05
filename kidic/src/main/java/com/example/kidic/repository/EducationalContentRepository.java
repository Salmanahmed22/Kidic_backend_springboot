package com.example.kidic.repository;

import com.example.kidic.entity.EducationalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationalContentRepository extends JpaRepository<EducationalContent, Long> {
    
    @Query("SELECT ec FROM EducationalContent ec WHERE ec.title LIKE %:title%")
    List<EducationalContent> findByTitleContaining(@Param("title") String title);
    
    @Query("SELECT ec FROM EducationalContent ec WHERE ec.estimatedTime = :estimatedTime")
    List<EducationalContent> findByEstimatedTime(@Param("estimatedTime") String estimatedTime);
    
    @Query("SELECT ec FROM EducationalContent ec WHERE ec.description LIKE %:description%")
    List<EducationalContent> findByDescriptionContaining(@Param("description") String description);
}
