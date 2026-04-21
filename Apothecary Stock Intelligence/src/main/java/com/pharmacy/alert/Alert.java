package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert implements Comparable<Alert> {
    private String alertId;
    private String title;
    private String message;
    private AlertSeverity severity;
    private String medicineId;
    private String medicineName;
    private LocalDateTime createdAt;
    private boolean acknowledged;
    private String type;

    public Alert(String title, String message, AlertSeverity severity, String medicineId, String medicineName, String type) {
        this.alertId = UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.acknowledged = false;
    }

    public String getSuppressionKey() {
        return type + ":" + medicineId;
    }

    @Override
    public int compareTo(Alert other) {
        int severityCompare = Integer.compare(this.severity.getPriority(), other.severity.getPriority());
        if (severityCompare != 0) return severityCompare;
        return other.createdAt.compareTo(this.createdAt);
    }
}
