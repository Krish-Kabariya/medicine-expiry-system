package com.pharmacy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "government_suppliers")
public class GovernmentSupplier extends Supplier {

    private String governmentOrderNumber;

    @Override
    public int getReturnWindowDays() {
        return 90;
    }

    @Override
    public String getReturnPolicy() {
        return "Returns accepted within 90 days. Full purchase price refund.";
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 1.0 * medicine.getQuantity();
    }
}
