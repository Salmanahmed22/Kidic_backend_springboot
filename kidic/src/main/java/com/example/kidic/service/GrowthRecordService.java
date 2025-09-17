package com.example.kidic.service;

import com.example.kidic.entity.Child;
import com.example.kidic.entity.GrowthRecord;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.repository.GrowthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class GrowthRecordService {

    @Autowired
    private GrowthRecordRepository growthRecordRepository;

    @Autowired
    private ChildRepository childRepository;

    public List<GrowthRecord> listForChild(Long childId) {
        return growthRecordRepository.findByChildId(childId);
    }

    public GrowthRecord addForChild(Long childId,
                                    String additionalInfo,
                                    LocalDate dateOfRecord,
                                    Double height,
                                    Double weight,
                                    GrowthRecord.GrowthType type,
                                    GrowthRecord.StatusType status) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        GrowthRecord record = new GrowthRecord(additionalInfo, dateOfRecord, height, weight, type, status, child);
        return growthRecordRepository.save(record);
    }

    public GrowthRecord editForChild(Long childId,
                                     Long recordId,
                                     String additionalInfo,
                                     LocalDate dateOfRecord,
                                     Double height,
                                     Double weight,
                                     GrowthRecord.GrowthType type,
                                     GrowthRecord.StatusType status) {
        GrowthRecord record = growthRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Growth record not found"));
        if (!record.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Record does not belong to child");
        }
        if (additionalInfo != null) record.setAdditionalInfo(additionalInfo);
        if (dateOfRecord != null) record.setDateOfRecord(dateOfRecord);
        if (height != null) record.setHeight(height);
        if (weight != null) record.setWeight(weight);
        if (type != null) record.setType(type);
        if (status != null) record.setStatus(status);
        return growthRecordRepository.save(record);
    }

    public void deleteForChild(Long childId, Long recordId) {
        GrowthRecord record = growthRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Growth record not found"));
        if (!record.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Record does not belong to child");
        }
        growthRecordRepository.delete(record);
    }
}


