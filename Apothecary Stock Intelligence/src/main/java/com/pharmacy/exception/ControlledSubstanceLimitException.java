package com.pharmacy.exception;

public class ControlledSubstanceLimitException extends RuntimeException {
    public ControlledSubstanceLimitException(String medicineName, int maxAllowed) {
        super(String.format("Controlled substance limit exceeded for %s. Maximum allowed per prescription: %d", medicineName, maxAllowed));
    }
}
