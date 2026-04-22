package com.pharmacy.service;

import com.pharmacy.dto.DashboardStatsDTO;
import com.pharmacy.dto.DispenseRequestDTO;
import com.pharmacy.dto.MedicineDTO;
import com.pharmacy.exception.ControlledSubstanceLimitException;
import com.pharmacy.exception.InsufficientStockException;
import com.pharmacy.exception.MedicineNotFoundException;
import com.pharmacy.model.*;
import com.pharmacy.model.enums.ScheduleType;
import com.pharmacy.repository.ConsumptionRecordRepository;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class InventoryService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private ExpiryPredictorService expiryPredictorService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private SupplierRepository supplierRepository; // 🔥 NEW

    // 🔥 ================= SUPPLIER MAPPING =================
    private String mapSupplierTypeToId(String supplierType) {
        if (supplierType == null) return null;

        return switch (supplierType.toUpperCase()) {
            case "LOCAL" -> supplierRepository.findAll().stream()
                    .filter(s -> s instanceof LocalSupplier)
                    .findFirst()
                    .map(Supplier::getSupplierId)
                    .orElseThrow(() -> new RuntimeException("Local supplier not found"));

            case "WHOLESALE" -> supplierRepository.findAll().stream()
                    .filter(s -> s instanceof WholesaleDistributor)
                    .findFirst()
                    .map(Supplier::getSupplierId)
                    .orElseThrow(() -> new RuntimeException("Wholesale supplier not found"));

            case "GOVERNMENT" -> supplierRepository.findAll().stream()
                    .filter(s -> s instanceof GovernmentSupplier)
                    .findFirst()
                    .map(Supplier::getSupplierId)
                    .orElseThrow(() -> new RuntimeException("Government supplier not found"));

            default -> throw new RuntimeException("Invalid supplier type");
        };
    }
    // 🔥 =====================================================

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(String id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));
    }

    @Transactional
    public Medicine addMedicine(MedicineDTO dto) {

        // 🔥 MAPPING APPLY HERE
        String actualSupplierId = mapSupplierTypeToId(dto.getSupplierId());

        Medicine medicine = Medicine.builder()
                .medicineId(dto.getMedicineId() != null ? dto.getMedicineId() : UUID.randomUUID().toString())
                .name(dto.getName())
                .batchNumber(dto.getBatchNumber())
                .expiryDate(dto.getExpiryDate())
                .manufactureDate(dto.getManufactureDate() != null ? dto.getManufactureDate() : LocalDate.now())
                .category(dto.getCategory())
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .isControlled(dto.isControlled())
                .scheduleType(dto.getScheduleType())
                .supplierId(actualSupplierId) // 🔥 IMPORTANT CHANGE
                .isRecalled(false)
                .isReturnable(true)
                .build();

        Medicine saved = medicineRepository.save(medicine);
        alertService.generateAlertsForAllMedicines();
        return saved;
    }

    @Transactional
    public Medicine updateMedicineStock(String id, int newQty) {
        Medicine medicine = getMedicineById(id);
        medicine.setQuantity(newQty);
        Medicine saved = medicineRepository.save(medicine);
        alertService.generateAlertsForAllMedicines();
        return saved;
    }

    @Transactional
    public void removeMedicine(String id) {
        Medicine medicine = getMedicineById(id);
        medicineRepository.delete(medicine);
        alertService.generateAlertsForAllMedicines();
    }

    public List<Medicine> searchMedicines(String query) {
        if (query == null || query.trim().isEmpty()) {
            return medicineRepository.findAll();
        }
        return medicineRepository.findByNameContainingIgnoreCase(query.trim());
    }

    public List<Medicine> getExpiringSoon(int days) {
        LocalDate now = LocalDate.now();
        LocalDate futureDate = now.plusDays(days);
        return medicineRepository.findByExpiryDateBetween(now, futureDate);
    }

    public List<Medicine> getExpiredMedicines() {
        return medicineRepository.findByExpiryDateBefore(LocalDate.now());
    }

    public List<Medicine> getLowStockMedicines(int threshold) {
        return medicineRepository.findLowStock(threshold);
    }

    @Transactional
    public Map<String, Object> dispenseMedicine(DispenseRequestDTO request) {
        Medicine medicine = getMedicineById(request.getMedicineId());

        if (medicine.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(medicine.getName(), request.getQuantity(), medicine.getQuantity());
        }

        if (medicine.isControlled() || medicine.getScheduleType() != ScheduleType.OTC) {
            int maxAllowed = medicine.getScheduleType().getMaxQtyPerPrescription();
            if (maxAllowed > 0 && request.getQuantity() > maxAllowed) {
                throw new ControlledSubstanceLimitException(medicine.getName(), maxAllowed);
            }
        }

        medicine.setQuantity(medicine.getQuantity() - request.getQuantity());
        medicineRepository.save(medicine);

        ConsumptionRecord record = ConsumptionRecord.builder()
                .medicineId(medicine.getMedicineId())
                .date(LocalDate.now())
                .quantityConsumed(request.getQuantity())
                .prescriptionId(request.getPrescriptionId())
                .build();
        consumptionRecordRepository.save(record);

        auditService.logDispense(medicine, request.getQuantity(), request.getPrescriptionId());
        alertService.generateAlertsForAllMedicines();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("medicineName", medicine.getName());
        response.put("dispensedQuantity", request.getQuantity());
        response.put("remainingStock", medicine.getQuantity());
        response.put("prescriptionId", request.getPrescriptionId());
        response.put("scheduleType", medicine.getScheduleType().getDescription());
        response.put("daysUntilExpiry", medicine.getDaysUntilExpiry());
        return response;
    }

    @Transactional
    public List<Medicine> processRecall(String batchNumber, String reason) {
        List<Medicine> affected = medicineRepository.findByBatchNumber(batchNumber);
        for (Medicine m : affected) {
            m.setRecalled(true);
            medicineRepository.save(m);
            auditService.logRecall(m);
        }
        alertService.generateAlertsForAllMedicines();
        return affected;
    }

    public DashboardStatsDTO getDashboardStats() {
        List<Medicine> all = medicineRepository.findAll();

        int totalMedicines = all.size();
        int expiringSoon = (int) all.stream().filter(m -> m.isExpiringSoon(30)).count();
        int expired = (int) all.stream().filter(Medicine::isExpired).count();
        int lowStock = (int) all.stream().filter(m -> m.getQuantity() < 10).count();
        double totalValue = all.stream()
                .mapToDouble(m -> m.getSellingPrice() * m.getQuantity())
                .sum();

        return DashboardStatsDTO.builder()
                .totalMedicines(totalMedicines)
                .expiringSoon(expiringSoon)
                .expired(expired)
                .lowStock(lowStock)
                .totalInventoryValue(totalValue)
                .criticalAlerts(alertService.getCriticalAlertCount())
                .activeAlerts(alertService.getActiveAlertCount())
                .build();
    }
}