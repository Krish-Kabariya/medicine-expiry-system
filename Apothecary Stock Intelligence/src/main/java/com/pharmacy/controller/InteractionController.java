package com.pharmacy.controller;

import com.pharmacy.dto.ApiResponseDTO;
import com.pharmacy.dto.InteractionCheckDTO;
import com.pharmacy.service.DrugInteractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
@CrossOrigin(origins = "*")
public class InteractionController {

    @Autowired
    private DrugInteractionService drugInteractionService;

    @PostMapping("/check")
    public ResponseEntity<ApiResponseDTO<List<DrugInteractionService.DrugInteraction>>> checkInteractions(
            @Valid @RequestBody InteractionCheckDTO dto) {
        List<DrugInteractionService.DrugInteraction> results = drugInteractionService.checkPrescription(dto.getMedicines());
        String message = results.isEmpty() ? "No interactions found" : results.size() + " interaction(s) detected";
        return ResponseEntity.ok(ApiResponseDTO.success(message, results));
    }
}
