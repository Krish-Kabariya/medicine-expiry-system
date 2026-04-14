package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.dto.DispenseRequestDTO;
import com.pharmacy.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dispense")
@CrossOrigin(origins = "*")
public class DispenseController {

    @Autowired private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> dispense(
            @Valid @RequestBody DispenseRequestDTO request) {
        Map<String, Object> result = inventoryService.dispenseMedicine(request);
        return ResponseEntity.ok(ApiResponseDTO.ok("Medicine dispensed successfully", result));
    }
}
