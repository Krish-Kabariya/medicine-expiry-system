package com.pharmacy.service;

import com.pharmacy.model.ConsumptionRecord;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.enums.MedicineCategory;
import com.pharmacy.repository.ConsumptionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpiryPredictorService {

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    public double getAverageDailyConsumption(String medicineId) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<ConsumptionRecord> records = consumptionRecordRepository
                .findByMedicineIdAndDateAfter(medicineId, thirtyDaysAgo);

        if (records.size() < 3) {
            return 0.0;
        }

        int totalConsumed = records.stream()
                .mapToInt(ConsumptionRecord::getQuantityConsumed)
                .sum();

        double baseRate = totalConsumed / 30.0;
        return baseRate;
    }

    public double getSeasonalAdjustedConsumption(String medicineId, MedicineCategory category) {
        double base = getAverageDailyConsumption(medicineId);
        return base * getSeasonalFactor(category, LocalDate.now());
    }

    public int predictDaysToSellOut(Medicine medicine) {
        double avg = getSeasonalAdjustedConsumption(medicine.getMedicineId(), medicine.getCategory());
        if (avg <= 0) return Integer.MAX_VALUE;
        return (int) (medicine.getQuantity() / avg);
    }

    public boolean predictWillExpireBeforeSold(Medicine medicine) {
        int daysToSell = predictDaysToSellOut(medicine);
        return daysToSell > medicine.getDaysUntilExpiry();
    }

    public int suggestDiscountPercent(Medicine medicine) {
        long daysLeft = medicine.getDaysUntilExpiry();
        if (daysLeft <= 0) return 0;
        if (daysLeft <= 15) return 40;
        if (daysLeft <= 30) return 30;
        if (daysLeft <= 45) return 20;
        if (daysLeft <= 60) return 10;
        return 0;
    }

    public double getSeasonalFactor(MedicineCategory category, LocalDate date) {
        int month = date.getMonthValue();
        return switch (category) {
            case ANTIHISTAMINE -> (month >= 3 && month <= 6) ? 1.5 : 0.8;
            case ANTACID -> (month >= 5 && month <= 9) ? 1.3 : 1.0;
            case VITAMIN -> (month >= 11 || month <= 2) ? 1.4 : 0.9;
            default -> 1.0;
        };
    }
}
