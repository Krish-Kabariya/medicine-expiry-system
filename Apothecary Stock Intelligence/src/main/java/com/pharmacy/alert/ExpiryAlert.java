package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExpiryAlert extends Alert {
    private long daysUntilExpiry;
    private int suggestedDiscount;

    public ExpiryAlert(String medicineId, String medicineName, long daysUntilExpiry, int suggestedDiscount) {
        super(
            "Expiry Warning: " + medicineName,
            String.format("%s expires in %d days. Suggested discount: %d%%", medicineName, daysUntilExpiry, suggestedDiscount),
            daysUntilExpiry <= 7 ? AlertSeverity.CRITICAL : daysUntilExpiry <= 15 ? AlertSeverity.HIGH : AlertSeverity.MEDIUM,
            medicineId,
            medicineName,
            "EXPIRY"
        );
        this.daysUntilExpiry = daysUntilExpiry;
        this.suggestedDiscount = suggestedDiscount;
    }
}
