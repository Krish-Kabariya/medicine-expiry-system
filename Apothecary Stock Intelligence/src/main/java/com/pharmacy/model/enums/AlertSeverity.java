package com.pharmacy.model.enums;

import lombok.Getter;

@Getter
public enum AlertSeverity {
    CRITICAL(1, "Critical"),
    HIGH(2, "High"),
    MEDIUM(3, "Medium"),
    LOW(4, "Low"),
    INFO(5, "Info");

    private final int priority;
    private final String displayName;

    AlertSeverity(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }
}
