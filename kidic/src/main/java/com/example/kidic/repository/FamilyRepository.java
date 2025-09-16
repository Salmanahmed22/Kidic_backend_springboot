package com.example.kidic.repository;

import com.example.kidic.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

    
    @Query("SELECT f FROM Family f JOIN f.parents p WHERE p.id = :parentId")
    List<Family> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT f FROM Family f JOIN f.children c WHERE c.id = :childId")
    Optional<Family> findByChildId(@Param("childId") Long childId);
}
