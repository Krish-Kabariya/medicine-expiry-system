package com.pharmacy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "government_suppliers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GovernmentSupplier extends Supplier {

    @Column(name = "government_order_number")
    private String governmentOrderNumber;

    @Override
    public int getReturnWindowDays() {
        return 90;
    }

    @Override
    public String getReturnPolicy() {
        return "Government supplier: 90-day return window, 100% refund on purchase price. Order#: " + governmentOrderNumber;
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 1.0 * medicine.getQuantity();
    }
}
