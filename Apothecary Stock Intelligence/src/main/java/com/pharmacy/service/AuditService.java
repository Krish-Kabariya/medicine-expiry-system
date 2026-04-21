package com.pharmacy.service;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.pharmacy.model.Medicine;

@Service
public class AuditService {

    @Value("${app.logs.path:./logs/}")
    private String logsPath;

    private final List<AuditRecord> records = new CopyOnWriteArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuditRecord {
        private String medicineId;
        private String medicineName;
        private LocalDateTime timestamp;
        private int quantity;
        private String prescriptionId;
        private String action;
    }

    public void logDispense(Medicine m, int qty, String prescriptionId) {
        AuditRecord record = AuditRecord.builder()
                .medicineId(m.getMedicineId())
                .medicineName(m.getName())
                .timestamp(LocalDateTime.now())
                .quantity(qty)
                .prescriptionId(prescriptionId)
                .action("DISPENSED")
                .build();
        records.add(record);
        writeToFile();
    }

    public void logReturn(Medicine m, int qty) {
        AuditRecord record = AuditRecord.builder()
                .medicineId(m.getMedicineId())
                .medicineName(m.getName())
                .timestamp(LocalDateTime.now())
                .quantity(qty)
                .prescriptionId(null)
                .action("RETURNED")
                .build();
        records.add(record);
        writeToFile();
    }

    public void logRecall(Medicine m) {
        AuditRecord record = AuditRecord.builder()
                .medicineId(m.getMedicineId())
                .medicineName(m.getName())
                .timestamp(LocalDateTime.now())
                .quantity(m.getQuantity())
                .prescriptionId(null)
                .action("RECALLED")
                .build();
        records.add(record);
        writeToFile();
    }

    public List<AuditRecord> getAllRecords() {
        return Collections.unmodifiableList(records);
    }

    public List<AuditRecord> getRecordsForMedicine(String medicineId) {
        return records.stream()
                .filter(r -> r.getMedicineId().equals(medicineId))
                .collect(Collectors.toList());
    }

    public void writeToFile() {
        try {
            File dir = new File(logsPath);
            if (!dir.exists()) dir.mkdirs();

            String filename = logsPath + "audit_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                AuditRecord latest = records.get(records.size() - 1);
                writer.write(String.format("[%s] %s | %s | Qty: %d | Rx: %s | %s%n",
                        latest.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        latest.getAction(),
                        latest.getMedicineName(),
                        latest.getQuantity(),
                        latest.getPrescriptionId() != null ? latest.getPrescriptionId() : "N/A",
                        latest.getMedicineId()));
            }
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        // Records are kept in memory; file is append-only backup
    }
}
