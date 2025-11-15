package com.aliyara.productionservice.service.interfaces;

import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.payload.ApiResponse;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO requestDTO);
    ProductResponseDTO  update(String id, ProductRequestDTO requestDTO);
    ApiResponse<Void> delete(String id);
    ProductResponseDTO  findById(String id);
    List<ProductResponseDTO> findAll();
    void decreaseStock(String productId, Integer quantity);
}
