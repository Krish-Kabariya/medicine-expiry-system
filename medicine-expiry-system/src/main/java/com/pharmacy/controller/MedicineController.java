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

    @Autowired private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getAllMedicines() {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.getAllMedicines()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Medicine>> getMedicineById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.getMedicineById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Medicine>> addMedicine(@Valid @RequestBody MedicineDTO dto) {
        Medicine medicine = inventoryService.addMedicine(dto);
        return ResponseEntity.ok(ApiResponseDTO.ok("Medicine added successfully", medicine));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponseDTO<Medicine>> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body) {
        int newQty = body.getOrDefault("quantity", 0);
        return ResponseEntity.ok(ApiResponseDTO.ok("Stock updated", inventoryService.updateMedicineStock(id, newQty)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> removeMedicine(@PathVariable String id) {
        inventoryService.removeMedicine(id);
        return ResponseEntity.ok(ApiResponseDTO.ok("Medicine removed", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.searchMedicines(q)));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.getExpiringSoon(days)));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getExpired() {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.getExpiredMedicines()));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponseDTO<List<Medicine>>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(ApiResponseDTO.ok(inventoryService.getLowStockMedicines(threshold)));
    }
}
