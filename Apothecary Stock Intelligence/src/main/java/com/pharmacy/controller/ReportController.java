package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.service.AuditService;
import com.pharmacy.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuditService auditService;

    @GetMapping("/wastage")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getWastageReport(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        if (month == 0) month = LocalDate.now().getMonthValue();
        if (year == 0) year = LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponseDTO.success(reportService.getWastageReport(month, year)));
    }

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponseDTO<List<Map<String, Object>>>> getExpiryForecast() {
        return ResponseEntity.ok(ApiResponseDTO.success(reportService.getExpiryForecastReport()));
    }

    @GetMapping("/valuation")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getStockValuation() {
        return ResponseEntity.ok(ApiResponseDTO.success(reportService.getStockValuationReport()));
    }

    @GetMapping("/audit")
    public ResponseEntity<ApiResponseDTO<List<AuditService.AuditRecord>>> getAuditReport() {
        return ResponseEntity.ok(ApiResponseDTO.success(auditService.getAllRecords()));
    }

    @PostMapping("/export")
    public ResponseEntity<ApiResponseDTO<String>> exportCsv(@RequestParam String type) {
        String filename = reportService.exportToCsv(type);
        return ResponseEntity.ok(ApiResponseDTO.success("Report exported successfully", filename));
    }
}
