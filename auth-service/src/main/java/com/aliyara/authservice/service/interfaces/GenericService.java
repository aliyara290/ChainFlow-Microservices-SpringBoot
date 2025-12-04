package com.aliyara.authservice.service.interfaces;

import com.aliyara.authservice.payload.ApiResponse;

import java.util.List;

public interface GenericService<T, R> {
    T create(R requestDTO);
    T update(R requestDTO, String id);
    ApiResponse<Void> delete(String id);
    List<T> getAll();
    T findById(String id);
}
