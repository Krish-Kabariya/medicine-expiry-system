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
@Table(name = "wholesale_distributors")
public class WholesaleDistributor extends Supplier {

    @Override
    public int getReturnWindowDays() {
        return 45;
    }

    @Override
    public String getReturnPolicy() {
        return "Returns accepted within 45 days. 90% refund of purchase price.";
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 0.90 * medicine.getQuantity();
    }
}
