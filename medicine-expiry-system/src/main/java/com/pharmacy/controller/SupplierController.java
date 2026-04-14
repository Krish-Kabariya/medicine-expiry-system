package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.model.Supplier;
import com.pharmacy.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Supplier>>> getAllSuppliers() {
        return ResponseEntity.ok(ApiResponseDTO.ok(supplierService.getAllSuppliers()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Supplier>> addSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponseDTO.ok("Supplier added", supplierService.addSupplier(supplier)));
    }

    @GetMapping("/returnable")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getReturnableStock() {
        return ResponseEntity.ok(ApiResponseDTO.ok(supplierService.getReturnableStock()));
    }

    @GetMapping("/return-report")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getReturnReport() {
        return ResponseEntity.ok(ApiResponseDTO.ok(supplierService.generateReturnReport()));
    }
}
