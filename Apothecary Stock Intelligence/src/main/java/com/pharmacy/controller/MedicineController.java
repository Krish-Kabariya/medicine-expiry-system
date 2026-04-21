package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.dto.MedicineDTO;
import com.pharmacy.model.Medicine;
import com.pharmacy.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
public class MedicineController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getAllMedicines() {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getAllMedicines()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Medicine>> getMedicineById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getMedicineById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Medicine>> addMedicine(@Valid @RequestBody MedicineDTO dto) {
        Medicine saved = inventoryService.addMedicine(dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Medicine added successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Medicine>> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body) {
        int newQty = body.getOrDefault("quantity", 0);
        Medicine updated = inventoryService.updateMedicineStock(id, newQty);
        return ResponseEntity.ok(ApiResponseDTO.success("Stock updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> removeMedicine(@PathVariable String id) {
        inventoryService.removeMedicine(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Medicine removed successfully", id));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.searchMedicines(q)));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getExpiringSoon(days)));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getExpired() {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getExpiredMedicines()));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(ApiResponseDTO.success(inventoryService.getLowStockMedicines(threshold)));
    }
}
