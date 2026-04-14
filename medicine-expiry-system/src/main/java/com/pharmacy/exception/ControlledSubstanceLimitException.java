package com.pharmacy.exception;

public class ControlledSubstanceLimitException extends RuntimeException {
    public ControlledSubstanceLimitException(String message) {
        super(message);
    }
}
