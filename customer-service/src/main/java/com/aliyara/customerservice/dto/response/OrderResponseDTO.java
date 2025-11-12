package com.aliyara.customerservice.dto.response;

import com.aliyara.customerservice.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private Integer quantity;
    private OrderStatus orderStatus;
    private String customerId;
    private String customerName;
    private List<String> productIds;
    private DeliveryResponseDTO delivery;
}