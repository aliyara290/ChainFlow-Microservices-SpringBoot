package com.aliyara.supplyservice.exception;

public class MaterialNotFoundException extends RuntimeException {
    public MaterialNotFoundException(String id) {
        super("Material with ID " + id + " notfound!");
    }
}
