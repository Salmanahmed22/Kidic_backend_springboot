package com.example.kidic.controller;

import com.example.kidic.entity.GrowthRecord;
import com.example.kidic.service.GrowthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/growth-records")
@CrossOrigin(origins = "*")
public class GrowthRecordController {

    @Autowired
    private GrowthRecordService growthRecordService;

    @GetMapping("/children/{childId}")
    public ResponseEntity<List<GrowthRecord>> listForChild(@PathVariable Long childId) {
        return ResponseEntity.ok(growthRecordService.listForChild(childId));
    }

    @PostMapping("/children/{childId}")
    public ResponseEntity<GrowthRecord> addForChild(
            @PathVariable Long childId,
            @RequestParam(value = "additionalInfo", required = false) String additionalInfo,
            @RequestParam("dateOfRecord") String dateOfRecord,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam("type") GrowthRecord.GrowthType type,
            @RequestParam("status") GrowthRecord.StatusType status) {
        GrowthRecord created = growthRecordService.addForChild(
                childId,
                additionalInfo,
                LocalDate.parse(dateOfRecord),
                height,
                weight,
                type,
                status
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/children/{childId}/{recordId}")
    public ResponseEntity<GrowthRecord> editForChild(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @RequestParam(value = "additionalInfo", required = false) String additionalInfo,
            @RequestParam(value = "dateOfRecord", required = false) String dateOfRecord,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "type", required = false) GrowthRecord.GrowthType type,
            @RequestParam(value = "status", required = false) GrowthRecord.StatusType status) {
        GrowthRecord updated = growthRecordService.editForChild(
                childId,
                recordId,
                additionalInfo,
                dateOfRecord != null ? LocalDate.parse(dateOfRecord) : null,
                height,
                weight,
                type,
                status
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/children/{childId}/{recordId}")
    public ResponseEntity<Void> deleteForChild(@PathVariable Long childId, @PathVariable Long recordId) {
        growthRecordService.deleteForChild(childId, recordId);
        return ResponseEntity.noContent().build();
    }
}


