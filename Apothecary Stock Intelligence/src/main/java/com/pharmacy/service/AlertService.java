package com.pharmacy.service;

import com.pharmacy.alert.*;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.enums.AlertSeverity;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class AlertService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ExpiryPredictorService expiryPredictorService;

    private final PriorityQueue<Alert> activeAlerts = new PriorityQueue<>();
    private final List<Alert> alertHistory = new CopyOnWriteArrayList<>();
    private final Set<String> suppressionKeys = Collections.synchronizedSet(new HashSet<>());

    public synchronized void generateAlertsForAllMedicines() {
        activeAlerts.clear();
        suppressionKeys.clear();

        List<Medicine> allMedicines = medicineRepository.findAll();

        for (Medicine medicine : allMedicines) {
            // Expiry alerts
            if (!medicine.isExpired() && medicine.isExpiringSoon(30)) {
                boolean willExpireBeforeSold = expiryPredictorService.predictWillExpireBeforeSold(medicine);
                if (willExpireBeforeSold || medicine.getDaysUntilExpiry() <= 15) {
                    int discount = expiryPredictorService.suggestDiscountPercent(medicine);
                    ExpiryAlert alert = new ExpiryAlert(
                            medicine.getMedicineId(),
                            medicine.getName(),
                            medicine.getDaysUntilExpiry(),
                            discount
                    );
                    addAlertIfNotSuppressed(alert);
                }
            }

            // Expired alerts
            if (medicine.isExpired()) {
                Alert alert = new Alert(
                        "EXPIRED: " + medicine.getName(),
                        medicine.getName() + " (Batch: " + medicine.getBatchNumber() + ") has expired. Remove from shelf immediately.",
                        AlertSeverity.CRITICAL,
                        medicine.getMedicineId(),
                        medicine.getName(),
                        "EXPIRED"
                );
                addAlertIfNotSuppressed(alert);
            }

            // Low stock alerts
            if (medicine.getQuantity() < 10 && !medicine.isRecalled()) {
                LowStockAlert alert = new LowStockAlert(
                        medicine.getMedicineId(),
                        medicine.getName(),
                        medicine.getQuantity()
                );
                addAlertIfNotSuppressed(alert);
            }

            // Recall alerts
            if (medicine.isRecalled()) {
                RecallAlert alert = new RecallAlert(
                        medicine.getMedicineId(),
                        medicine.getName(),
                        medicine.getBatchNumber(),
                        "Product recall in effect"
                );
                addAlertIfNotSuppressed(alert);
            }
        }
    }

    private void addAlertIfNotSuppressed(Alert alert) {
        String key = alert.getSuppressionKey();
        if (!suppressionKeys.contains(key)) {
            suppressionKeys.add(key);
            activeAlerts.add(alert);
        }
    }

    public void addAlert(Alert alert) {
        activeAlerts.add(alert);
        alertHistory.add(alert);
    }

    public synchronized boolean acknowledgeAlert(String alertId) {
        List<Alert> remaining = new ArrayList<>();
        boolean found = false;
        while (!activeAlerts.isEmpty()) {
            Alert alert = activeAlerts.poll();
            if (alert.getAlertId().equals(alertId)) {
                alert.setAcknowledged(true);
                alertHistory.add(alert);
                found = true;
            } else {
                remaining.add(alert);
            }
        }
        activeAlerts.addAll(remaining);
        return found;
    }

    public List<Alert> getActiveAlerts() {
        List<Alert> sorted = new ArrayList<>(activeAlerts);
        Collections.sort(sorted);
        return sorted;
    }

    public List<Alert> getAlertHistory() {
        return Collections.unmodifiableList(alertHistory);
    }

    public void clearSuppression() {
        suppressionKeys.clear();
    }

    public int getCriticalAlertCount() {
        return (int) activeAlerts.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL)
                .count();
    }

    public int getActiveAlertCount() {
        return activeAlerts.size();
    }
}
