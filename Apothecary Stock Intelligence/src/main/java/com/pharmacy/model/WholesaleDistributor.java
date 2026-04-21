package com.pharmacy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wholesale_distributors")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WholesaleDistributor extends Supplier {

    @Override
    public int getReturnWindowDays() {
        return 45;
    }

    @Override
    public String getReturnPolicy() {
        return "Wholesale distributor: 45-day return window, 90% refund on purchase price";
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 0.90 * medicine.getQuantity();
    }
}
