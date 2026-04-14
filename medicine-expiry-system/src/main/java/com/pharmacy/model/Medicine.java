package com.pharmacy.model;

import com.pharmacy.model.enums.MedicineCategory;
import com.pharmacy.model.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medicines")
public class Medicine {

    @Id
    private String medicineId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private LocalDate manufactureDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineCategory category;

    private int quantity;

    private double purchasePrice;

    private double sellingPrice;

    private boolean isControlled;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private String supplierId;

    private boolean isRecalled;

    private boolean isReturnable;

    @PrePersist
    public void prePersist() {
        if (medicineId == null || medicineId.isEmpty()) {
            medicineId = UUID.randomUUID().toString();
        }
    }

    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon(int days) {
        long daysLeft = getDaysUntilExpiry();
        return daysLeft >= 0 && daysLeft <= days;
    }
}
