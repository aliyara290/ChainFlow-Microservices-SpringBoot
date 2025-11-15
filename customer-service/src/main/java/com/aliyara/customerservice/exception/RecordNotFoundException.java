package com.aliyara.customerservice.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message + "not found");
    }
}