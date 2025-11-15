package com.aliyara.customerservice.dto.request;

import com.aliyara.customerservice.dto.request.productionClient.ProductRequestDTO;
import com.aliyara.customerservice.model.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private OrderStatus orderStatus;
    private String customerId;
    private List<ProductRequestDTO> products;
}