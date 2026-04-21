package com.pharmacy.model;

import com.pharmacy.model.enums.MedicineCategory;
import com.pharmacy.model.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @Column(name = "medicine_id")
    private String medicineId;

    @Column(nullable = false)
    private String name;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineCategory category;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "purchase_price")
    private double purchasePrice;

    @Column(name = "selling_price")
    private double sellingPrice;

    @Column(name = "is_controlled")
    private boolean isControlled;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "is_recalled")
    private boolean isRecalled;

    @Column(name = "is_returnable")
    private boolean isReturnable;

    @PrePersist
    public void generateId() {
        if (this.medicineId == null || this.medicineId.isEmpty()) {
            this.medicineId = UUID.randomUUID().toString();
        }
    }

    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon(int days) {
        if (isExpired()) return false;
        return getDaysUntilExpiry() <= days;
    }
}
