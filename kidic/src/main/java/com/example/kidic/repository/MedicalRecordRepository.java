package com.example.kidic.repository;

import com.example.kidic.entity.MedicalRecord;
import com.example.kidic.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    List<MedicalRecord> findByChild(Child child);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.child.id = :childId")
    List<MedicalRecord> findByChildId(@Param("childId") Long childId);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.type = :type")
    List<MedicalRecord> findByType(@Param("type") MedicalRecord.MedicalRecordType type);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.status = :status")
    List<MedicalRecord> findByStatus(@Param("status") MedicalRecord.StatusType status);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.dateOfRecord BETWEEN :startDate AND :endDate")
    List<MedicalRecord> findByDateOfRecordBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.child.id = :childId AND mr.type = :type")
    List<MedicalRecord> findByChildIdAndType(@Param("childId") Long childId, @Param("type") MedicalRecord.MedicalRecordType type);
    
    long countByChild(Child child);
    
    long countByChildAndType(Child child, MedicalRecord.MedicalRecordType type);
}
