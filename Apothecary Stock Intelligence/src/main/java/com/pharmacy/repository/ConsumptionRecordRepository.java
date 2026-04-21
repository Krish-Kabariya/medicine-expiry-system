package com.pharmacy.repository;

import com.pharmacy.model.ConsumptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsumptionRecordRepository extends JpaRepository<ConsumptionRecord, Long> {

    List<ConsumptionRecord> findByMedicineId(String medicineId);

    List<ConsumptionRecord> findByMedicineIdAndDateAfter(String medicineId, LocalDate date);
}
