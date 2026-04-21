package com.pharmacy.service;

import com.pharmacy.model.ConsumptionRecord;
import com.pharmacy.model.Medicine;
import com.pharmacy.repository.ConsumptionRecordRepository;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private ExpiryPredictorService expiryPredictorService;

    @Autowired
    private AuditService auditService;

    @Value("${app.reports.path:./reports/}")
    private String reportsPath;

    public Map<String, Object> getWastageReport(int month, int year) {
        List<Medicine> expired = medicineRepository.findByExpiryDateBefore(LocalDate.now());

        List<Medicine> filteredExpired = expired.stream()
                .filter(m -> m.getExpiryDate().getMonthValue() == month && m.getExpiryDate().getYear() == year)
                .collect(Collectors.toList());

        double totalWastageValue = filteredExpired.stream()
                .mapToDouble(m -> m.getPurchasePrice() * m.getQuantity())
                .sum();

        double totalSellingLoss = filteredExpired.stream()
                .mapToDouble(m -> m.getSellingPrice() * m.getQuantity())
                .sum();

        Map<String, Long> byCategory = filteredExpired.stream()
                .collect(Collectors.groupingBy(m -> m.getCategory().getDisplayName(), Collectors.counting()));

        List<Map<String, Object>> details = filteredExpired.stream().map(m -> {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("name", m.getName());
            detail.put("batch", m.getBatchNumber());
            detail.put("category", m.getCategory().getDisplayName());
            detail.put("quantity", m.getQuantity());
            detail.put("expiryDate", m.getExpiryDate().toString());
            detail.put("purchaseLoss", m.getPurchasePrice() * m.getQuantity());
            detail.put("sellingLoss", m.getSellingPrice() * m.getQuantity());
            return detail;
        }).collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", String.format("%02d/%d", month, year));
        report.put("totalExpiredItems", filteredExpired.size());
        report.put("totalWastageValue", totalWastageValue);
        report.put("totalSellingLoss", totalSellingLoss);
        report.put("byCategory", byCategory);
        report.put("details", details);
        return report;
    }

    public List<Map<String, Object>> getExpiryForecastReport() {
        List<Medicine> allMedicines = medicineRepository.findAll();

        return allMedicines.stream()
                .filter(m -> !m.isExpired())
                .sorted(Comparator.comparing(Medicine::getExpiryDate))
                .map(m -> {
                    Map<String, Object> forecast = new LinkedHashMap<>();
                    forecast.put("name", m.getName());
                    forecast.put("batch", m.getBatchNumber());
                    forecast.put("category", m.getCategory().getDisplayName());
                    forecast.put("quantity", m.getQuantity());
                    forecast.put("expiryDate", m.getExpiryDate().toString());
                    forecast.put("daysUntilExpiry", m.getDaysUntilExpiry());
                    forecast.put("predictedDaysToSellOut", expiryPredictorService.predictDaysToSellOut(m));
                    forecast.put("willExpireBeforeSold", expiryPredictorService.predictWillExpireBeforeSold(m));
                    forecast.put("suggestedDiscount", expiryPredictorService.suggestDiscountPercent(m));
                    forecast.put("seasonalFactor", expiryPredictorService.getSeasonalFactor(m.getCategory(), LocalDate.now()));
                    forecast.put("avgDailyConsumption", expiryPredictorService.getAverageDailyConsumption(m.getMedicineId()));

                    String risk;
                    long days = m.getDaysUntilExpiry();
                    if (days <= 15) risk = "CRITICAL";
                    else if (days <= 30) risk = "HIGH";
                    else if (days <= 60) risk = "MEDIUM";
                    else risk = "LOW";
                    forecast.put("riskLevel", risk);

                    return forecast;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStockValuationReport() {
        List<Medicine> all = medicineRepository.findAll();

        double totalPurchaseValue = all.stream()
                .mapToDouble(m -> m.getPurchasePrice() * m.getQuantity())
                .sum();

        double totalSellingValue = all.stream()
                .mapToDouble(m -> m.getSellingPrice() * m.getQuantity())
                .sum();

        double potentialProfit = totalSellingValue - totalPurchaseValue;

        Map<String, Double> valuationByCategory = all.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategory().getDisplayName(),
                        Collectors.summingDouble(m -> m.getSellingPrice() * m.getQuantity())
                ));

        double expiredValue = all.stream()
                .filter(Medicine::isExpired)
                .mapToDouble(m -> m.getPurchasePrice() * m.getQuantity())
                .sum();

        double atRiskValue = all.stream()
                .filter(m -> m.isExpiringSoon(30) && !m.isExpired())
                .mapToDouble(m -> m.getSellingPrice() * m.getQuantity())
                .sum();

        List<Map<String, Object>> details = all.stream()
                .sorted(Comparator.comparingDouble((Medicine m) -> m.getSellingPrice() * m.getQuantity()).reversed())
                .map(m -> {
                    Map<String, Object> detail = new LinkedHashMap<>();
                    detail.put("name", m.getName());
                    detail.put("batch", m.getBatchNumber());
                    detail.put("category", m.getCategory().getDisplayName());
                    detail.put("quantity", m.getQuantity());
                    detail.put("purchasePrice", m.getPurchasePrice());
                    detail.put("sellingPrice", m.getSellingPrice());
                    detail.put("totalPurchaseValue", m.getPurchasePrice() * m.getQuantity());
                    detail.put("totalSellingValue", m.getSellingPrice() * m.getQuantity());
                    detail.put("margin", m.getSellingPrice() - m.getPurchasePrice());
                    detail.put("status", m.isExpired() ? "EXPIRED" : m.isExpiringSoon(30) ? "EXPIRING_SOON" : "ACTIVE");
                    return detail;
                })
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("generatedDate", LocalDate.now().toString());
        report.put("totalItems", all.size());
        report.put("totalPurchaseValue", totalPurchaseValue);
        report.put("totalSellingValue", totalSellingValue);
        report.put("potentialProfit", potentialProfit);
        report.put("expiredStockValue", expiredValue);
        report.put("atRiskValue", atRiskValue);
        report.put("valuationByCategory", valuationByCategory);
        report.put("details", details);
        return report;
    }

    public String exportToCsv(String reportType) {
        try {
            File dir = new File(reportsPath);
            if (!dir.exists()) dir.mkdirs();

            String filename = reportsPath + reportType + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                List<Medicine> medicines = medicineRepository.findAll();

                switch (reportType.toLowerCase()) {
                    case "inventory":
                        writer.println("Name,Batch,Category,Quantity,Purchase Price,Selling Price,Expiry Date,Days Left,Status");
                        for (Medicine m : medicines) {
                            writer.printf("%s,%s,%s,%d,%.2f,%.2f,%s,%d,%s%n",
                                    m.getName(), m.getBatchNumber(), m.getCategory().getDisplayName(),
                                    m.getQuantity(), m.getPurchasePrice(), m.getSellingPrice(),
                                    m.getExpiryDate(), m.getDaysUntilExpiry(),
                                    m.isExpired() ? "EXPIRED" : m.isExpiringSoon(30) ? "EXPIRING" : "ACTIVE");
                        }
                        break;

                    case "expiry":
                        writer.println("Name,Batch,Category,Quantity,Expiry Date,Days Left,Risk Level,Suggested Discount");
                        for (Medicine m : medicines) {
                            if (!m.isExpired()) {
                                String risk = m.getDaysUntilExpiry() <= 15 ? "CRITICAL" :
                                        m.getDaysUntilExpiry() <= 30 ? "HIGH" :
                                                m.getDaysUntilExpiry() <= 60 ? "MEDIUM" : "LOW";
                                writer.printf("%s,%s,%s,%d,%s,%d,%s,%d%%%n",
                                        m.getName(), m.getBatchNumber(), m.getCategory().getDisplayName(),
                                        m.getQuantity(), m.getExpiryDate(), m.getDaysUntilExpiry(),
                                        risk, expiryPredictorService.suggestDiscountPercent(m));
                            }
                        }
                        break;

                    case "audit":
                        writer.println("Timestamp,Medicine,Action,Quantity,Prescription ID");
                        for (AuditService.AuditRecord r : auditService.getAllRecords()) {
                            writer.printf("%s,%s,%s,%d,%s%n",
                                    r.getTimestamp(), r.getMedicineName(), r.getAction(),
                                    r.getQuantity(), r.getPrescriptionId() != null ? r.getPrescriptionId() : "N/A");
                        }
                        break;

                    default:
                        writer.println("Name,Batch,Category,Quantity,Selling Price,Total Value");
                        for (Medicine m : medicines) {
                            writer.printf("%s,%s,%s,%d,%.2f,%.2f%n",
                                    m.getName(), m.getBatchNumber(), m.getCategory().getDisplayName(),
                                    m.getQuantity(), m.getSellingPrice(), m.getSellingPrice() * m.getQuantity());
                        }
                }
            }
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }
}
