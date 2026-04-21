package com.pharmacy.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {
    private String alertId;
    private String title;
    private String message;
    private String severity;
    private String medicineId;
    private String medicineName;
    private String createdAt;
    private boolean acknowledged;
    private String type;
}
