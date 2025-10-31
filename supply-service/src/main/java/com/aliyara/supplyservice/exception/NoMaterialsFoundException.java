package com.aliyara.supplyservice.exception;

public class NoMaterialsFoundException extends RuntimeException {
    public NoMaterialsFoundException() {
        super("No materials found!");
    }
}
