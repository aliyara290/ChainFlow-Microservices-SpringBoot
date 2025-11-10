package com.aliyara.customerservice.exception;

public class FailedToInsertDataException extends RuntimeException {
    public FailedToInsertDataException(String message) {
        super(message);
    }
}
