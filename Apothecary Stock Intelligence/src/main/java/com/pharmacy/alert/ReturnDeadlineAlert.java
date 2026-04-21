package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReturnDeadlineAlert extends Alert {
    private long daysUntilDeadline;
    private double recoveryValue;

    public ReturnDeadlineAlert(String medicineId, String medicineName, long daysUntilDeadline, double recoveryValue) {
        super(
            "Return Deadline Approaching: " + medicineName,
            String.format("Return window for %s closes in %d days. Recovery value: ₹%.2f", medicineName, daysUntilDeadline, recoveryValue),
            daysUntilDeadline <= 7 ? AlertSeverity.HIGH : AlertSeverity.MEDIUM,
            medicineId,
            medicineName,
            "RETURN_DEADLINE"
        );
        this.daysUntilDeadline = daysUntilDeadline;
        this.recoveryValue = recoveryValue;
    }
}
