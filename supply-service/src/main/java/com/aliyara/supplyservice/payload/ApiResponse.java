package com.aliyara.supplyservice.payload;

public record ApiResponse<T>(
        Boolean status,
        String message,
        T data
) {
}
