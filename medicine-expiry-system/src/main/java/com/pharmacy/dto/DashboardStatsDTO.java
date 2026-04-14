package com.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private int totalMedicines;
    private int expiringSoon;
    private int expired;
    private int lowStock;
    private double totalInventoryValue;
    private int criticalAlerts;
    private int activeAlerts;
}
