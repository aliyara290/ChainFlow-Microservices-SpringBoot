package com.aliyara.productionservice.payload;

public record ApiResponse<T>(
        Boolean status,
        String message,
        T data
) {
}
