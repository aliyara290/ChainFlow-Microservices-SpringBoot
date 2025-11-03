package com.aliyara.productionservice.exception;


public class NoRecordsFoundException extends RuntimeException {
    public NoRecordsFoundException(String record) {
        super(record + " not found");
    }
}