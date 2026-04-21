package com.pharmacy.config;

import com.pharmacy.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AlertEngineConfig {

    @Autowired
    private AlertService alertService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void periodicAlertGeneration() {
        alertService.generateAlertsForAllMedicines();
    }
}
