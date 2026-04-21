package com.pharmacy.model;

import com.pharmacy.model.enums.SupplierType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_suppliers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LocalSupplier extends Supplier {

    @Override
    public int getReturnWindowDays() {
        return 30;
    }

    @Override
    public String getReturnPolicy() {
        return "Local supplier: 30-day return window, 80% refund on purchase price";
    }

    @Override
    public double calculateReturnValue(Medicine medicine) {
        return medicine.getPurchasePrice() * 0.80 * medicine.getQuantity();
    }
}
