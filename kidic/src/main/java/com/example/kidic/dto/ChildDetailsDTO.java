package com.example.kidic.dto;

import com.example.kidic.entity.DiseaseAndAllergy;
import com.example.kidic.entity.GrowthRecord;
import com.example.kidic.entity.Meal;
import com.example.kidic.entity.MedicalRecord;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ChildDetailsDTO {
    private Long id;
    private String name;
    private Boolean gender;
    private LocalDate dateOfBirth;
    private String medicalNotes;
    private List<MedicalRecord> medicalRecords;
    private List<GrowthRecord> growthRecords;
    private List<DiseaseAndAllergy> diseasesAndAllergies;
    private List<Meal> meals;

}
