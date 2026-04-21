package com.pharmacy.model;

import com.pharmacy.model.enums.SupplierType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Supplier {

    @Id
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_number")
    private String contactNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierType type;

    @Column(name = "last_order_date")
    private LocalDate lastOrderDate;

    @PrePersist
    public void generateId() {
        if (this.supplierId == null || this.supplierId.isEmpty()) {
            this.supplierId = UUID.randomUUID().toString();
        }
    }

    public abstract int getReturnWindowDays();

    public abstract String getReturnPolicy();

    public abstract double calculateReturnValue(Medicine medicine);

    public boolean canReturn(Medicine medicine) {
        if (medicine.isExpired()) return false;
        if (medicine.isRecalled()) return true;
        long daysSinceManufacture = ChronoUnit.DAYS.between(
                medicine.getManufactureDate() != null ? medicine.getManufactureDate() : LocalDate.now().minusDays(90),
                LocalDate.now()
        );
        return daysSinceManufacture <= getReturnWindowDays() && medicine.isReturnable();
    }
}
