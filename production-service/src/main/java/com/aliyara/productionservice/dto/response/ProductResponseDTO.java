package com.aliyara.productionservice.dto.response;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private String id;
    private String name;
    private Integer productionTime;
    private double price;
    private Integer stock;
}
