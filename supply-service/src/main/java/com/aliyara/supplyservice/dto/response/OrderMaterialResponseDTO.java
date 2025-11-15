package com.aliyara.supplyservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class OrderMaterialResponseDTO {
    private String id;
    private String materialId;
    private String materialName;
    private Integer quantity;
    private String unit;
}
