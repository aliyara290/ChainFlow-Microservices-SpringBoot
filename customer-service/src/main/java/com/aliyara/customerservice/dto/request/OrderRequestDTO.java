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
//    @NotNull(message = "Quantity is required")
//    private Integer quantity;

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Product IDs are required")
    private List<ProductRequestDTO> products;
}