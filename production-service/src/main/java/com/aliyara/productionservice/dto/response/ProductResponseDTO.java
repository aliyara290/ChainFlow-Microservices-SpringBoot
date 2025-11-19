package com.aliyara.productionservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponseDTO {
    private String id;
    private String name;
    private Integer productionTime;
    private double price;
    private Integer stock;
}
