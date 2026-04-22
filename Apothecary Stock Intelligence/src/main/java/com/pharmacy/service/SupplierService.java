package com.pharmacy.service;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Supplier;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepository.findById(id);
    }

    public Map<String, Object> getReturnableStock() {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Supplier supplier : suppliers) {

            List<Medicine> medicines =
                    medicineRepository.findBySupplierId(supplier.getSupplierId());

            List<Medicine> returnable = medicines.stream()
                    .filter(supplier::canReturn)
                    .collect(Collectors.toList());

            if (!returnable.isEmpty()) {

                double totalValue = returnable.stream()
                        .mapToDouble(supplier::calculateReturnValue)
                        .sum();

                Map<String, Object> entry = new LinkedHashMap<>();

                entry.put("supplier", Map.of(
                        "id", supplier.getSupplierId(),
                        "name", supplier.getName(),
                        "type", supplier.getType().getDisplayName(),
                        "returnPolicy", supplier.getReturnPolicy(),
                        "returnWindowDays", supplier.getReturnWindowDays()
                ));

                entry.put("returnableMedicines",
                        returnable.stream().map(m -> Map.of(
                                "id", m.getMedicineId(),
                                "name", m.getName(),
                                "batch", m.getBatchNumber(),
                                "quantity", m.getQuantity(),
                                "returnValue", supplier.calculateReturnValue(m)

                        )).collect(Collectors.toList())
                );

                entry.put("totalRecoveryValue", totalValue);
                result.add(entry);
            }
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("suppliers", result);
        report.put("totalRecoverableValue",
                result.stream()
                        .mapToDouble(r -> (double) r.get("totalRecoveryValue"))
                        .sum());

        return report;
    }

    public Map<String, Object> generateReturnReport() {

        // Simply reuse polymorphic logic
        return getReturnableStock();
    }
}