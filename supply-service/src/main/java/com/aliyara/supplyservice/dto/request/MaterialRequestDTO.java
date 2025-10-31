package com.aliyara.supplyservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MaterialRequestDTO {
    private String id;
    private Integer stock;
    private Integer stockMin;
    private String unit;
    private String supplierId;
}
