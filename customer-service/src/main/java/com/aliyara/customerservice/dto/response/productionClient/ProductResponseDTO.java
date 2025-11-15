package com.aliyara.customerservice.dto.response.productionClient;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
    private String id;
    private String name;
    private Integer productionTime;
    private double price;
    private Integer stock;
}
