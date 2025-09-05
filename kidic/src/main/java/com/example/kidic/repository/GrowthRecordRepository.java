package com.example.kidic.repository;

import com.example.kidic.entity.GrowthRecord;
import com.example.kidic.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GrowthRecordRepository extends JpaRepository<GrowthRecord, Long> {
    
    List<GrowthRecord> findByChild(Child child);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.child.id = :childId")
    List<GrowthRecord> findByChildId(@Param("childId") Long childId);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.type = :type")
    List<GrowthRecord> findByType(@Param("type") GrowthRecord.GrowthType type);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.status = :status")
    List<GrowthRecord> findByStatus(@Param("status") GrowthRecord.StatusType status);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.dateOfRecord BETWEEN :startDate AND :endDate")
    List<GrowthRecord> findByDateOfRecordBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.child.id = :childId AND gr.type = :type")
    List<GrowthRecord> findByChildIdAndType(@Param("childId") Long childId, @Param("type") GrowthRecord.GrowthType type);
    
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.child.id = :childId AND gr.status = :status")
    List<GrowthRecord> findByChildIdAndStatus(@Param("childId") Long childId, @Param("status") GrowthRecord.StatusType status);
    
    long countByChild(Child child);
    
    long countByChildAndType(Child child, GrowthRecord.GrowthType type);
    
    long countByChildAndStatus(Child child, GrowthRecord.StatusType status);
}
