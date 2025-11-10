package com.aliyara.productionservice.exception;

public class FailedToSaveDataException extends RuntimeException {
    public FailedToSaveDataException(String message) {
        super(message);
    }
}