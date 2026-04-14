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
@Table(name = "local_suppliers")
public class LocalSupplier extends Supplier {

    @Override
    public int getReturnWindowDays() {
        return 30;
    }

    @Override
    public String getReturnPolicy() {
        return "Returns accepted within 30 days. 80% refund of purchase price.";
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 0.80 * medicine.getQuantity();
    }
}
