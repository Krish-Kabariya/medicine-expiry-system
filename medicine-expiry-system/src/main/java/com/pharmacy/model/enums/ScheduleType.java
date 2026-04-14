package com.pharmacy.model.enums;

public enum ScheduleType {
    OTC("Over the Counter", 0),
    SCHEDULE_H("Schedule H", 30),
    SCHEDULE_H1("Schedule H1", 10);

    private final String description;
    private final int maxQtyPerPrescription;

    ScheduleType(String description, int maxQtyPerPrescription) {
        this.description = description;
        this.maxQtyPerPrescription = maxQtyPerPrescription;
    }

    public String getDescription() { return description; }
    public int getMaxQtyPerPrescription() { return maxQtyPerPrescription; }
}
