package com.pharmacy.model.enums;

public enum AlertSeverity {
    LOW(1),
    MODERATE(2),
    HIGH(3),
    CRITICAL(4);

    private final int level;

    AlertSeverity(int level) { this.level = level; }
    public int getLevel() { return level; }
}
