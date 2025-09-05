package com.example.kidic.repository;

import com.example.kidic.entity.DiseaseAndAllergy;
import com.example.kidic.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseAndAllergyRepository extends JpaRepository<DiseaseAndAllergy, Long> {
    
    List<DiseaseAndAllergy> findByChild(Child child);
    
    @Query("SELECT da FROM DiseaseAndAllergy da WHERE da.child.id = :childId")
    List<DiseaseAndAllergy> findByChildId(@Param("childId") Long childId);
    
    @Query("SELECT da FROM DiseaseAndAllergy da WHERE da.type = :type")
    List<DiseaseAndAllergy> findByType(@Param("type") DiseaseAndAllergy.DiseaseAllergyType type);
    
    @Query("SELECT da FROM DiseaseAndAllergy da WHERE da.description LIKE %:description%")
    List<DiseaseAndAllergy> findByDescriptionContaining(@Param("description") String description);
    
    @Query("SELECT da FROM DiseaseAndAllergy da WHERE da.aiResponse IS NOT NULL AND da.aiResponse != ''")
    List<DiseaseAndAllergy> findByAiResponseNotNull();
    
    long countByChild(Child child);
    
    long countByChildAndType(Child child, DiseaseAndAllergy.DiseaseAllergyType type);
}
