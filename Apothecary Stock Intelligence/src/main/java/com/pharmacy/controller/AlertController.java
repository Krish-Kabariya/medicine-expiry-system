package com.pharmacy.controller;

import com.pharmacy.alert.Alert;
import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Alert>>> getActiveAlerts() {
        return ResponseEntity.ok(ApiResponseDTO.success(alertService.getActiveAlerts()));
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponseDTO<Boolean>> acknowledgeAlert(@PathVariable String id) {
        boolean result = alertService.acknowledgeAlert(id);
        if (result) {
            return ResponseEntity.ok(ApiResponseDTO.success("Alert acknowledged", true));
        }
        return ResponseEntity.badRequest().body(ApiResponseDTO.error("Alert not found"));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<List<Alert>>> getAlertHistory() {
        return ResponseEntity.ok(ApiResponseDTO.success(alertService.getAlertHistory()));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ApiResponseDTO<String>> regenerateAlerts() {
        alertService.generateAlertsForAllMedicines();
        return ResponseEntity.ok(ApiResponseDTO.success("Alerts regenerated", "OK"));
    }
}
