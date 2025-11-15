package com.aliyara.customerservice.dto.request.productionClient;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductRequestDTO {
    private String productId;
    private Integer quantity;
}
