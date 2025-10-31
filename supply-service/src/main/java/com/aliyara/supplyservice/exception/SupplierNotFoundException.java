package com.aliyara.supplyservice.exception;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String id) {
        super("Supplier not found with ID: " + id);
    }
}
