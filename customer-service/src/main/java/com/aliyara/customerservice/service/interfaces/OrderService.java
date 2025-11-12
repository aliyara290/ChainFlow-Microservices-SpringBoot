package com.aliyara.customerservice.service.interfaces;

import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.dto.response.OrderResponseDTO;
import com.aliyara.customerservice.payload.ApiResponse;

import java.util.List;

public interface OrderService {
    OrderResponseDTO create(OrderRequestDTO requestDTO);
    OrderResponseDTO update(String id, OrderRequestDTO requestDTO);
    ApiResponse<Void> delete(String id);
    OrderResponseDTO findById(String id);
    List<OrderResponseDTO> findAll();
    List<OrderResponseDTO> findByCustomerId(String customerId);
    Boolean isInStock(String productId, Integer quantity);
    void updateProductStock(String productId, Integer quantity);
}