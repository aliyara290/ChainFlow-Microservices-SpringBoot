package com.aliyara.authservice.payload;

public record ApiResponse<T>(
        Boolean status,
        String message,
        T data
) {
}
