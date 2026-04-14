package com.pharmacy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DispenseRequestDTO {

    @NotBlank(message = "Medicine ID is required")
    private String medicineId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String prescriptionId;
}
