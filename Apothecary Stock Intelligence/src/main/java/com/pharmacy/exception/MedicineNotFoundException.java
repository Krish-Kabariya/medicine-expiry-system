package com.pharmacy.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(String id) {
        super("Medicine not found with ID: " + id);
    }
}
