package com.pharmacy.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String medicineName, int requested, int available) {
        super(String.format("Insufficient stock for %s. Requested: %d, Available: %d", medicineName, requested, available));
    }
}
