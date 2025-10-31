package com.aliyara.supplyservice.exception;

public class NoSupplierFoundException extends RuntimeException {
    public NoSupplierFoundException() {
        super("No supplier found!");
    }
}
