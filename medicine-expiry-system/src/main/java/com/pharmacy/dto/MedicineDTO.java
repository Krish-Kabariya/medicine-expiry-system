package com.pharmacy.dto;

import com.pharmacy.model.enums.MedicineCategory;
import com.pharmacy.model.enums.ScheduleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicineDTO {

    @NotBlank(message = "Medicine name is required")
    private String name;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private LocalDate manufactureDate;

    @NotNull(message = "Category is required")
    private MedicineCategory category;

    @Min(value = 0, message = "Quantity must be non-negative")
    private int quantity;

    @Min(value = 0, message = "Purchase price must be non-negative")
    private double purchasePrice;

    @Min(value = 0, message = "Selling price must be non-negative")
    private double sellingPrice;

    private boolean isControlled;

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    private String supplierId;

    private boolean isReturnable;
}
