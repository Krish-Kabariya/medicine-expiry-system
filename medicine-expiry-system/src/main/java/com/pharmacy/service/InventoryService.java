package com.pharmacy.service;

import com.pharmacy.dto.DispenseRequestDTO;
import com.pharmacy.dto.DashboardStatsDTO;
import com.pharmacy.dto.MedicineDTO;
import com.pharmacy.exception.InsufficientStockException;
import com.pharmacy.exception.MedicineNotFoundException;
import com.pharmacy.exception.ControlledSubstanceLimitException;
import com.pharmacy.model.ConsumptionRecord;
import com.pharmacy.model.Medicine;
import com.pharmacy.repository.ConsumptionRecordRepository;
import com.pharmacy.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final MedicineRepository medicineRepository;
    private final ConsumptionRecordRepository consumptionRecordRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(String id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found: " + id));
    }

    public Medicine addMedicine(MedicineDTO dto) {
        Medicine medicine = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name(dto.getName())
                .batchNumber(dto.getBatchNumber())
                .expiryDate(dto.getExpiryDate())
                .manufactureDate(dto.getManufactureDate())
                .category(dto.getCategory())
                .quantity(dto.getQuantity())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .isControlled(dto.isControlled())
                .scheduleType(dto.getScheduleType())
                .supplierId(dto.getSupplierId())
                .isRecalled(false)
                .isReturnable(dto.isReturnable())
                .build();
        return medicineRepository.save(medicine);
    }

    public Medicine updateMedicineStock(String id, int newQty) {
        Medicine medicine = getMedicineById(id);
        medicine.setQuantity(newQty);
        return medicineRepository.save(medicine);
    }

    public void removeMedicine(String id) {
        getMedicineById(id);
        medicineRepository.deleteById(id);
    }

    public List<Medicine> searchMedicines(String query) {
        return medicineRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Medicine> getExpiringSoon(int days) {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(days);
        return medicineRepository.findByExpiryDateBetween(from, to);
    }

    public List<Medicine> getExpiredMedicines() {
        return medicineRepository.findByExpiryDateBefore(LocalDate.now());
    }

    public List<Medicine> getLowStockMedicines(int threshold) {
        return medicineRepository.findLowStock(threshold);
    }

    public Map<String, Object> dispenseMedicine(DispenseRequestDTO request) {
        Medicine medicine = getMedicineById(request.getMedicineId());

        if (medicine.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                "Insufficient stock. Available: " + medicine.getQuantity());
        }

        if (medicine.isControlled()) {
            int max = medicine.getScheduleType().getMaxQtyPerPrescription();
            if (request.getQuantity() > max) {
                throw new ControlledSubstanceLimitException(
                    "Exceeds controlled substance limit of " + max);
            }
        }

        medicine.setQuantity(medicine.getQuantity() - request.getQuantity());
        medicineRepository.save(medicine);

        ConsumptionRecord record = new ConsumptionRecord();
        record.setMedicineId(medicine.getMedicineId());
        record.setDate(LocalDate.now());
        record.setQuantityConsumed(request.getQuantity());
        record.setPrescriptionId(request.getPrescriptionId());
        consumptionRecordRepository.save(record);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("medicineName", medicine.getName());
        response.put("dispensedQty", request.getQuantity());
        response.put("remainingStock", medicine.getQuantity());
        return response;
    }

    public List<Medicine> processRecall(String batchNumber, String reason) {
        List<Medicine> affected = medicineRepository.findByBatchNumber(batchNumber);
        for (Medicine m : affected) {
            m.setRecalled(true);
            medicineRepository.save(m);
        }
        return affected;
    }

    public DashboardStatsDTO getDashboardStats() {
        List<Medicine> all = medicineRepository.findAll();

        int expiringSoon = (int) all.stream()
                .filter(m -> m.isExpiringSoon(30)).count();
        int expired = (int) all.stream()
                .filter(Medicine::isExpired).count();
        int lowStock = medicineRepository.findLowStock(10).size();
        double totalValue = all.stream()
                .mapToDouble(m -> m.getSellingPrice() * m.getQuantity())
                .sum();

        return DashboardStatsDTO.builder()
                .totalMedicines(all.size())
                .expiringSoon(expiringSoon)
                .expired(expired)
                .lowStock(lowStock)
                .totalInventoryValue(totalValue)
                .criticalAlerts(0)
                .activeAlerts(0)
                .build();
    }
}