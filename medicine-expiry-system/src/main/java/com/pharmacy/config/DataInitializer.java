package com.pharmacy.config;

import com.pharmacy.model.*;
import com.pharmacy.model.enums.MedicineCategory;
import com.pharmacy.model.enums.ScheduleType;
import com.pharmacy.model.enums.SupplierType;
import com.pharmacy.repository.ConsumptionRecordRepository;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired private MedicineRepository medicineRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private ConsumptionRecordRepository consumptionRecordRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (medicineRepository.count() > 0) {
            return;
        }

        // --- Suppliers ---
        WholesaleDistributor wholesale = new WholesaleDistributor();
        wholesale.setSupplierId(UUID.randomUUID().toString());
        wholesale.setName("Ram Medical Wholesale");
        wholesale.setContactNumber("9876543210");
        wholesale.setAddress("12, Industrial Area, Sector 5, Mumbai");
        wholesale.setType(SupplierType.WHOLESALE);
        wholesale.setLastOrderDate(LocalDate.now().minusDays(15));
        supplierRepository.save(wholesale);

        LocalSupplier local = new LocalSupplier();
        local.setSupplierId(UUID.randomUUID().toString());
        local.setName("City Pharma Local");
        local.setContactNumber("9123456789");
        local.setAddress("45, Market Street, Pune");
        local.setType(SupplierType.LOCAL);
        local.setLastOrderDate(LocalDate.now().minusDays(7));
        supplierRepository.save(local);

        GovernmentSupplier govt = new GovernmentSupplier();
        govt.setSupplierId(UUID.randomUUID().toString());
        govt.setName("Government Medical Store");
        govt.setContactNumber("0222-3344556");
        govt.setAddress("Civil Hospital Compound, New Delhi");
        govt.setType(SupplierType.GOVERNMENT);
        govt.setLastOrderDate(LocalDate.now().minusDays(30));
        govt.setGovernmentOrderNumber("GOV-2024-MED-0045");
        supplierRepository.save(govt);

        // --- Medicines ---
        List<Medicine> medicines = new ArrayList<>();

        medicines.add(buildMedicine("Paracetamol 500mg", "BATCH-PAR-001", 20,
                MedicineCategory.ANALGESIC, ScheduleType.OTC, 50, 2.50, 5.00,
                false, local.getSupplierId(), true));

        medicines.add(buildMedicine("Amoxicillin 500mg", "BATCH-AMX-002", 10,
                MedicineCategory.ANTIBIOTIC, ScheduleType.SCHEDULE_H, 30, 12.00, 22.00,
                true, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("Metformin 500mg", "BATCH-MET-003", 180,
                MedicineCategory.DIABETIC, ScheduleType.SCHEDULE_H, 200, 3.50, 7.00,
                true, govt.getSupplierId(), false));

        medicines.add(buildMedicine("Atorvastatin 10mg", "BATCH-ATV-004", 300,
                MedicineCategory.CARDIAC, ScheduleType.SCHEDULE_H, 150, 8.00, 15.00,
                true, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("ORS Powder", "BATCH-ORS-005", 45,
                MedicineCategory.ANTACID, ScheduleType.OTC, 80, 5.00, 10.00,
                false, local.getSupplierId(), true));

        medicines.add(buildMedicine("Hep B Vaccine", "BATCH-HEP-006", 60,
                MedicineCategory.VACCINE, ScheduleType.SCHEDULE_H1, 20, 150.00, 280.00,
                true, govt.getSupplierId(), false));

        medicines.add(buildMedicine("Warfarin 5mg", "BATCH-WAR-007", 90,
                MedicineCategory.CARDIAC, ScheduleType.SCHEDULE_H1, 60, 15.00, 28.00,
                true, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("Azithromycin 500mg", "BATCH-AZI-008", 25,
                MedicineCategory.ANTIBIOTIC, ScheduleType.SCHEDULE_H, 45, 18.00, 35.00,
                true, local.getSupplierId(), true));

        medicines.add(buildMedicine("Cetirizine 10mg", "BATCH-CET-009", 365,
                MedicineCategory.ANTIHISTAMINE, ScheduleType.OTC, 300, 1.50, 4.00,
                false, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("Pantoprazole 40mg", "BATCH-PAN-010", 5,
                MedicineCategory.ANTACID, ScheduleType.SCHEDULE_H, 100, 4.50, 9.00,
                false, local.getSupplierId(), true));

        medicines.add(buildMedicine("Metronidazole 400mg", "BATCH-MTZ-011", 15,
                MedicineCategory.ANTIBIOTIC, ScheduleType.SCHEDULE_H, 70, 3.00, 6.50,
                false, govt.getSupplierId(), true));

        medicines.add(buildMedicine("Amlodipine 5mg", "BATCH-AML-012", 240,
                MedicineCategory.CARDIAC, ScheduleType.SCHEDULE_H, 120, 5.50, 11.00,
                true, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("Omeprazole 20mg", "BATCH-OME-013", 120,
                MedicineCategory.ANTACID, ScheduleType.OTC, 90, 3.80, 7.50,
                false, local.getSupplierId(), true));

        medicines.add(buildMedicine("Vitamin D3 1000IU", "BATCH-VTD-014", 400,
                MedicineCategory.VITAMIN, ScheduleType.OTC, 250, 2.00, 5.00,
                false, wholesale.getSupplierId(), true));

        medicines.add(buildMedicine("Ibuprofen 400mg", "BATCH-IBU-015", 35,
                MedicineCategory.ANALGESIC, ScheduleType.OTC, 160, 3.00, 6.00,
                false, local.getSupplierId(), true));

        medicineRepository.saveAll(medicines);

        // --- Consumption Records ---
        Random rand = new Random(42);
        for (int medIdx = 0; medIdx < 5; medIdx++) {
            Medicine med = medicines.get(medIdx);
            int numRecords = 15 + rand.nextInt(6);
            for (int i = 0; i < numRecords; i++) {
                int daysAgo = rand.nextInt(30);
                int qty = 1 + rand.nextInt(5);
                ConsumptionRecord record = new ConsumptionRecord(
                        null,
                        med.getMedicineId(),
                        LocalDate.now().minusDays(daysAgo),
                        qty,
                        "INIT-RX-" + (1000 + i));
                consumptionRecordRepository.save(record);
            }
        }

        System.out.println("✅ Sample data loaded: 15 medicines, 3 suppliers, consumption history initialized.");
    }

    private Medicine buildMedicine(String name, String batch, int daysToExpiry,
                                    MedicineCategory category, ScheduleType schedule,
                                    int quantity, double purchasePrice, double sellingPrice,
                                    boolean isControlled, String supplierId, boolean isReturnable) {
        return Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name(name)
                .batchNumber(batch)
                .expiryDate(LocalDate.now().plusDays(daysToExpiry))
                .manufactureDate(LocalDate.now().minusDays(180))
                .category(category)
                .quantity(quantity)
                .purchasePrice(purchasePrice)
                .sellingPrice(sellingPrice)
                .isControlled(isControlled)
                .scheduleType(schedule)
                .supplierId(supplierId)
                .isRecalled(false)
                .isReturnable(isReturnable)
                .build();
    }
}