package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.enums.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByBatchNumber(String batchNumber);

    List<Medicine> findByCategory(MedicineCategory category);

    List<Medicine> findByIsRecalledTrue();

    List<Medicine> findByExpiryDateBefore(LocalDate date);

    List<Medicine> findByExpiryDateBetween(LocalDate from, LocalDate to);

    List<Medicine> findBySupplierId(String supplierId);

    @Query("SELECT m FROM Medicine m WHERE m.quantity < :threshold")
    List<Medicine> findLowStock(@Param("threshold") int threshold);
}
