package com.example.kidic.repository;

import com.example.kidic.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    
    Optional<Parent> findByEmail(String email);
    
    List<Parent> findByGender(Boolean gender);
    
    @Query("SELECT p FROM Parent p WHERE p.name LIKE %:name%")
    List<Parent> findByNameContaining(@Param("name") String name);
    
//    @Query("SELECT p FROM Parent p JOIN p.children c WHERE c.id = :childId")
//    List<Parent> findByChildId(@Param("childId") Long childId);
    
    boolean existsByEmail(String email);
}
