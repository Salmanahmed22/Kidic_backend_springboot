package com.example.kidic.repository;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.Parent;
import com.example.kidic.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    
//    List<Child> findByParent(Parent parent);
    
    List<Child> findByFamily(Family family);
    
    @Query("SELECT c FROM Child c WHERE c.name LIKE %:name%")
    List<Child> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Child c WHERE c.gender = :gender")
    List<Child> findByGender(@Param("gender") Boolean gender);
    
    @Query("SELECT c FROM Child c WHERE c.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Child> findByBirthDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
//    @Query("SELECT c FROM Child c WHERE c.parent.id = :parentId")
//    List<Child> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Child c WHERE c.family.id = :familyId")
    List<Child> findByFamilyId(@Param("familyId") UUID familyId);
    
//    long countByParent(Parent parent);
    
    long countByFamily(Family family);
}