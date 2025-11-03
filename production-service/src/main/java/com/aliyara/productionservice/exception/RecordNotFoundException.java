package com.aliyara.productionservice.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String record) {
        super(record + " not found");
    }
}
