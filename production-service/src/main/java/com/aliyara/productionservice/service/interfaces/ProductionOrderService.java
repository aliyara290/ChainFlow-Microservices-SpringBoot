package com.aliyara.productionservice.service.interfaces;

import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.model.ProductionOrder;
import com.aliyara.productionservice.payload.ApiResponse;

import java.util.List;

public interface ProductionOrderService {
    ProductionOrderResponseDTO create(ProductionOrderRequestDTO requestDTO);
    ProductionOrderResponseDTO  update(String id, ProductionOrderRequestDTO requestDTO);
    ApiResponse<Void> delete(String id);
    ProductionOrderResponseDTO  findById(String id);
    List<ProductionOrderResponseDTO> findAll();
}
