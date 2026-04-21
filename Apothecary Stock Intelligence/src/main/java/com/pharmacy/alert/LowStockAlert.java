package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LowStockAlert extends Alert {
    private int currentStock;

    public LowStockAlert(String medicineId, String medicineName, int currentStock) {
        super(
            "Low Stock: " + medicineName,
            String.format("%s has only %d units remaining. Reorder recommended.", medicineName, currentStock),
            currentStock <= 5 ? AlertSeverity.HIGH : AlertSeverity.MEDIUM,
            medicineId,
            medicineName,
            "LOW_STOCK"
        );
        this.currentStock = currentStock;
    }
}
