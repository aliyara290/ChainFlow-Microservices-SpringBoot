package com.aliyara.customerservice.exception.productionServiceExceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
