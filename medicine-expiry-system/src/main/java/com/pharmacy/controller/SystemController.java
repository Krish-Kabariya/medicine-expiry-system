package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.dto.DashboardStatsDTO;
import com.pharmacy.model.Medicine;
import com.pharmacy.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SystemController {

    private final InventoryService inventoryService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = inventoryService.getDashboardStats();
        return ResponseEntity.ok(ApiResponseDTO.<DashboardStatsDTO>builder()
                .success(true)
                .message("Dashboard stats fetched")
                .data(stats)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/recall")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> processRecall(
            @RequestBody Map<String, String> body) {
        String batchNumber = body.get("batchNumber");
        String reason = body.get("reason");
        List<Medicine> affected = inventoryService.processRecall(batchNumber, reason);
        return ResponseEntity.ok(ApiResponseDTO.<List<Medicine>>builder()
                .success(true)
                .message("Recall processed. Affected: " + affected.size())
                .data(affected)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponseDTO<String>> triggerSave() {
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("Data saved successfully")
                .data("OK")
                .timestamp(LocalDateTime.now())
                .build());
    }
}