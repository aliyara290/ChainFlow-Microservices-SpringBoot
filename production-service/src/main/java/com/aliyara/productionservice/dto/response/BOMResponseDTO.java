package com.aliyara.productionservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@NoArgsConstructor
//@Builder
public class BOMResponseDTO {
    private String id;
    private Integer quantity;
    private String materialId;
    private String productId;
}
