package com.aliyara.customerservice.service.interfaces;


import com.aliyara.customerservice.dto.request.DeliveryRequestDTO;
import com.aliyara.customerservice.dto.response.DeliveryResponseDTO;

import java.util.List;

public interface DeliveryService {
    DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryRequestDTO);
    DeliveryResponseDTO updateDelivery(DeliveryRequestDTO deliveryRequestDTO, String id);
    DeliveryResponseDTO getDelivery(String id);
    List<DeliveryResponseDTO> getDeliveries();
}