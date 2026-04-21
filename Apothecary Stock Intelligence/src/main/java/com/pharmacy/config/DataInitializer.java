package com.pharmacy.config;

import com.pharmacy.model.*;
import com.pharmacy.model.enums.*;
import com.pharmacy.repository.*;
import com.pharmacy.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private AlertService alertService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (medicineRepository.count() > 0) return;

        // --- Create Suppliers ---
        WholesaleDistributor wholesale = new WholesaleDistributor();
        wholesale.setSupplierId(UUID.randomUUID().toString());
        wholesale.setName("Ram Medical Wholesale");
        wholesale.setContactNumber("+91-9876543210");
        wholesale.setAddress("45, MG Road, Mumbai 400001");
        wholesale.setType(SupplierType.WHOLESALE);
        wholesale.setLastOrderDate(LocalDate.now().minusDays(15));
        supplierRepository.save(wholesale);

        LocalSupplier local = new LocalSupplier();
        local.setSupplierId(UUID.randomUUID().toString());
        local.setName("City Pharma Local");
        local.setContactNumber("+91-9988776655");
        local.setAddress("12, Station Road, Pune 411001");
        local.setType(SupplierType.LOCAL);
        local.setLastOrderDate(LocalDate.now().minusDays(10));
        supplierRepository.save(local);

        GovernmentSupplier govt = new GovernmentSupplier();
        govt.setSupplierId(UUID.randomUUID().toString());
        govt.setName("Government Medical Store");
        govt.setContactNumber("+91-1800-111-222");
        govt.setAddress("Civil Hospital Complex, Delhi 110001");
        govt.setType(SupplierType.GOVERNMENT);
        govt.setLastOrderDate(LocalDate.now().minusDays(30));
        govt.setGovernmentOrderNumber("GOV-2025-MED-0042");
        supplierRepository.save(govt);

        // --- Create Medicines ---
        LocalDate now = LocalDate.now();

        Medicine m1 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Paracetamol 500mg").batchNumber("PCM-2025-001")
                .expiryDate(now.plusDays(20)).manufactureDate(now.minusMonths(6))
                .category(MedicineCategory.ANALGESIC).quantity(50)
                .purchasePrice(12.0).sellingPrice(18.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m2 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Amoxicillin 500mg").batchNumber("AMX-2025-012")
                .expiryDate(now.plusDays(10)).manufactureDate(now.minusMonths(8))
                .category(MedicineCategory.ANTIBIOTIC).quantity(30)
                .purchasePrice(25.0).sellingPrice(40.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m3 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Metformin 500mg").batchNumber("MET-2025-045")
                .expiryDate(now.plusDays(180)).manufactureDate(now.minusMonths(3))
                .category(MedicineCategory.DIABETIC).quantity(200)
                .purchasePrice(8.0).sellingPrice(15.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(local.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m4 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Atorvastatin 10mg").batchNumber("ATV-2025-078")
                .expiryDate(now.plusDays(300)).manufactureDate(now.minusMonths(2))
                .category(MedicineCategory.CARDIAC).quantity(150)
                .purchasePrice(18.0).sellingPrice(30.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m5 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("ORS Powder").batchNumber("ORS-2025-033")
                .expiryDate(now.plusDays(45)).manufactureDate(now.minusMonths(4))
                .category(MedicineCategory.ANTACID).quantity(80)
                .purchasePrice(5.0).sellingPrice(10.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(local.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m6 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Hep B Vaccine").batchNumber("HBV-2025-007")
                .expiryDate(now.plusDays(60)).manufactureDate(now.minusMonths(3))
                .category(MedicineCategory.VACCINE).quantity(20)
                .purchasePrice(150.0).sellingPrice(250.0)
                .isControlled(true).scheduleType(ScheduleType.SCHEDULE_H1)
                .supplierId(govt.getSupplierId())
                .isRecalled(false).isReturnable(false)
                .build();

        Medicine m7 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Warfarin 5mg").batchNumber("WRF-2025-019")
                .expiryDate(now.plusDays(90)).manufactureDate(now.minusMonths(4))
                .category(MedicineCategory.CARDIAC).quantity(60)
                .purchasePrice(22.0).sellingPrice(35.0)
                .isControlled(true).scheduleType(ScheduleType.SCHEDULE_H1)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m8 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Azithromycin 500mg").batchNumber("AZM-2025-055")
                .expiryDate(now.plusDays(25)).manufactureDate(now.minusMonths(5))
                .category(MedicineCategory.ANTIBIOTIC).quantity(45)
                .purchasePrice(30.0).sellingPrice(50.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(local.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m9 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Cetirizine 10mg").batchNumber("CTZ-2025-090")
                .expiryDate(now.plusDays(365)).manufactureDate(now.minusMonths(1))
                .category(MedicineCategory.ANTIHISTAMINE).quantity(300)
                .purchasePrice(6.0).sellingPrice(12.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m10 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Pantoprazole 40mg").batchNumber("PNT-2025-022")
                .expiryDate(now.plusDays(5)).manufactureDate(now.minusMonths(10))
                .category(MedicineCategory.ANTACID).quantity(100)
                .purchasePrice(10.0).sellingPrice(20.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m11 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Metronidazole 400mg").batchNumber("MTZ-2025-061")
                .expiryDate(now.plusDays(15)).manufactureDate(now.minusMonths(7))
                .category(MedicineCategory.ANTIBIOTIC).quantity(70)
                .purchasePrice(14.0).sellingPrice(22.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(local.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m12 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Amlodipine 5mg").batchNumber("AML-2025-044")
                .expiryDate(now.plusDays(240)).manufactureDate(now.minusMonths(2))
                .category(MedicineCategory.CARDIAC).quantity(120)
                .purchasePrice(15.0).sellingPrice(25.0)
                .isControlled(false).scheduleType(ScheduleType.SCHEDULE_H)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m13 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Omeprazole 20mg").batchNumber("OMP-2025-038")
                .expiryDate(now.plusDays(120)).manufactureDate(now.minusMonths(3))
                .category(MedicineCategory.ANTACID).quantity(90)
                .purchasePrice(9.0).sellingPrice(16.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(local.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m14 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Vitamin D3 1000IU").batchNumber("VTD-2025-015")
                .expiryDate(now.plusDays(400)).manufactureDate(now.minusMonths(1))
                .category(MedicineCategory.VITAMIN).quantity(250)
                .purchasePrice(7.0).sellingPrice(14.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(govt.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        Medicine m15 = Medicine.builder()
                .medicineId(UUID.randomUUID().toString())
                .name("Ibuprofen 400mg").batchNumber("IBU-2025-027")
                .expiryDate(now.plusDays(35)).manufactureDate(now.minusMonths(5))
                .category(MedicineCategory.ANALGESIC).quantity(160)
                .purchasePrice(10.0).sellingPrice(18.0)
                .isControlled(false).scheduleType(ScheduleType.OTC)
                .supplierId(wholesale.getSupplierId())
                .isRecalled(false).isReturnable(true)
                .build();

        medicineRepository.save(m1);
        medicineRepository.save(m2);
        medicineRepository.save(m3);
        medicineRepository.save(m4);
        medicineRepository.save(m5);
        medicineRepository.save(m6);
        medicineRepository.save(m7);
        medicineRepository.save(m8);
        medicineRepository.save(m9);
        medicineRepository.save(m10);
        medicineRepository.save(m11);
        medicineRepository.save(m12);
        medicineRepository.save(m13);
        medicineRepository.save(m14);
        medicineRepository.save(m15);

        // --- Create Consumption Records (last 30 days) ---
        createConsumptionHistory(m1.getMedicineId(), new int[]{3,2,4,1,3,2,5,1,2,3,4,2,3,1,2,3,2,4,1,3});
        createConsumptionHistory(m2.getMedicineId(), new int[]{2,1,3,2,1,2,1,3,2,1,2,1,3,2,1,2,1,3,2,1});
        createConsumptionHistory(m3.getMedicineId(), new int[]{5,4,6,3,5,4,7,3,5,4,6,3,5,4,7,3,5,4,6,3});
        createConsumptionHistory(m4.getMedicineId(), new int[]{2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3});
        createConsumptionHistory(m5.getMedicineId(), new int[]{4,3,5,2,4,3,5,2,4,3,5,2,4,3,5,2,4,3,5,2});

        // Generate initial alerts
        alertService.generateAlertsForAllMedicines();

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   Medicine Expiry & Stock Intelligence System           ║");
        System.out.println("║   Sample data loaded successfully!                      ║");
        System.out.println("║   15 medicines, 3 suppliers, 100 consumption records    ║");
        System.out.println("║                                                         ║");
        System.out.println("║   Dashboard: http://localhost:8080                       ║");
        System.out.println("║   H2 Console: http://localhost:8080/h2-console          ║");
        System.out.println("║   API Base: http://localhost:8080/api                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    private void createConsumptionHistory(String medicineId, int[] quantities) {
        LocalDate now = LocalDate.now();
        for (int i = 0; i < quantities.length; i++) {
            ConsumptionRecord record = ConsumptionRecord.builder()
                    .medicineId(medicineId)
                    .date(now.minusDays(30 - i))
                    .quantityConsumed(quantities[i])
                    .prescriptionId("RX-INIT-" + String.format("%04d", i + 1))
                    .build();
            consumptionRecordRepository.save(record);
        }
    }
}
