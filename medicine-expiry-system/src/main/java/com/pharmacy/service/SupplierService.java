package com.pharmacy.service;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Supplier;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SupplierService {

    @Autowired private SupplierRepository supplierRepository;
    @Autowired private MedicineRepository medicineRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Map<String, Object> getReturnableStock() {
        List<Supplier> suppliers = supplierRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        for (Supplier supplier : suppliers) {
            List<Medicine> medicines = medicineRepository.findBySupplierId(supplier.getSupplierId());
            List<Map<String, Object>> returnable = new ArrayList<>();

            for (Medicine medicine : medicines) {
                if (supplier.canReturn(medicine)) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("medicineId", medicine.getMedicineId());
                    entry.put("medicineName", medicine.getName());
                    entry.put("batchNumber", medicine.getBatchNumber());
                    entry.put("quantity", medicine.getQuantity());
                    entry.put("daysUntilExpiry", medicine.getDaysUntilExpiry());
                    entry.put("returnValue", supplier.calculateReturnValue(medicine));
                    returnable.add(entry);
                }
            }

            if (!returnable.isEmpty()) {
                Map<String, Object> supplierData = new LinkedHashMap<>();
                supplierData.put("supplierId", supplier.getSupplierId());
                supplierData.put("supplierName", supplier.getName());
                supplierData.put("supplierType", supplier.getType());
                supplierData.put("returnPolicy", supplier.getReturnPolicy());
                supplierData.put("returnWindowDays", supplier.getReturnWindowDays());
                supplierData.put("returnableMedicines", returnable);
                supplierData.put("totalReturnValue",
                        returnable.stream().mapToDouble(m -> (Double) m.get("returnValue")).sum());
                result.put(supplier.getSupplierId(), supplierData);
            }
        }
        return result;
    }

    public Map<String, Object> generateReturnReport() {
        Map<String, Object> returnableStock = getReturnableStock();
        double grandTotal = returnableStock.values().stream()
                .mapToDouble(v -> {
                    if (v instanceof Map<?, ?> map) {
                        Object val = map.get("totalReturnValue");
                        return val instanceof Number n ? n.doubleValue() : 0.0;
                    }
                    return 0.0;
                }).sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("generatedAt", new Date().toString());
        report.put("totalPotentialRecovery", grandTotal);
        report.put("supplierBreakdown", returnableStock);
        return report;
    }
}
