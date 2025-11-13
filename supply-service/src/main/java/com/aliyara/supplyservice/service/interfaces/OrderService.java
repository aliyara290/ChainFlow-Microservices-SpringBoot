package com.aliyara.supplyservice.service.interfaces;

import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;

public interface OrderService extends GenericService<OrderResponseDTO, OrderRequestDTO> {
    boolean updateOrderStatus(String oderId, String status);
}
