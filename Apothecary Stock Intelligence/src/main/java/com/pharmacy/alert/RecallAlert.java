package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecallAlert extends Alert {
    private String batchNumber;
    private String reason;

    public RecallAlert(String medicineId, String medicineName, String batchNumber, String reason) {
        super(
            "RECALL: " + medicineName + " (Batch: " + batchNumber + ")",
            String.format("Medicine %s batch %s has been recalled. Reason: %s. Remove from shelf immediately.", medicineName, batchNumber, reason),
            AlertSeverity.CRITICAL,
            medicineId,
            medicineName,
            "RECALL"
        );
        this.batchNumber = batchNumber;
        this.reason = reason;
    }
}
