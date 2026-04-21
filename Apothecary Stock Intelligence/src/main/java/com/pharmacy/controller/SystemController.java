package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.dto.DashboardStatsDTO;
import com.pharmacy.model.Medicine;
import com.pharmacy.service.AuditService;
import com.pharmacy.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
public class SystemController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AuditService auditService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<DashboardStatsDTO>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getDashboardStats()));
    }

    @PostMapping("/recall")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> processRecall(@RequestBody Map<String, String> body) {
        String batchNumber = body.get("batchNumber");
        String reason = body.getOrDefault("reason", "No reason provided");
        List<Medicine> affected = inventoryService.processRecall(batchNumber, reason);
        return ResponseEntity.ok(ApiResponseDTO.success(
                affected.size() + " medicine(s) recalled", affected));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDTO<String>> triggerSave() {
        auditService.writeToFile();
        return ResponseEntity.ok(ApiResponseDTO.success("Data saved successfully", "OK"));
    }
}
