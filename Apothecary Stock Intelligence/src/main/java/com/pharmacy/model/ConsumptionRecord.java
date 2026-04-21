package com.pharmacy.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consumption_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_id", nullable = false)
    private String medicineId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "quantity_consumed", nullable = false)
    private int quantityConsumed;

    @Column(name = "prescription_id")
    private String prescriptionId;
}
