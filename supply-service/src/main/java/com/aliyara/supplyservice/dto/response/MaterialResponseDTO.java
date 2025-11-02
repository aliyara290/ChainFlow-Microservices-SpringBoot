package com.aliyara.supplyservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MaterialResponseDTO {
    private String id;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;
    private String supplierId;
}
