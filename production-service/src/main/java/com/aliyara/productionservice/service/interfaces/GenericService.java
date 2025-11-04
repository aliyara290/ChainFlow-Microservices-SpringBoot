package com.aliyara.productionservice.service.interfaces;

import com.aliyara.productionservice.payload.ApiResponse;
import java.util.List;

public interface GenericService<T, P> {
    T create(P t);
    T update(String id, P t);
    ApiResponse<Void> delete(String id);
    T findById(String id);
    List<T> findAll();
    Boolean isExistById(String id);
}
