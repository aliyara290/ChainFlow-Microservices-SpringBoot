package com.aliyara.productionservice.dto.response.material;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialDTO {
    private String id;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;
    private String supplierId;
}