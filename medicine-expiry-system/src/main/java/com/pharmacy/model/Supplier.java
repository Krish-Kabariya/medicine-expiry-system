package com.pharmacy.model;

import com.pharmacy.model.enums.SupplierType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "suppliers")
public abstract class Supplier {

    @Id
    private String supplierId;

    private String name;

    private String contactNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private SupplierType type;

    private LocalDate lastOrderDate;

    @PrePersist
    public void prePersist() {
        if (supplierId == null || supplierId.isEmpty()) {
            supplierId = UUID.randomUUID().toString();
        }
    }

    public abstract int getReturnWindowDays();

    public abstract String getReturnPolicy();

    public abstract double calculateReturnValue(Medicine medicine);

    public boolean canReturn(Medicine medicine) {
        if (!medicine.isReturnable()) return false;
        if (medicine.isExpired()) return false;
        long daysUntilExpiry = medicine.getDaysUntilExpiry();
        return daysUntilExpiry > 0 && daysUntilExpiry <= getReturnWindowDays();
    }
}
