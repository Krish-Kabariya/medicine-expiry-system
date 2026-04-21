package com.pharmacy.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispenseRequestDTO {

    @NotBlank(message = "Medicine ID is required")
    private String medicineId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String prescriptionId;
}
