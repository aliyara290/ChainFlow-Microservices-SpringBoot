package com.aliyara.supplyservice.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMaterialRequestDTO {
    private String materialId;
    private Integer quantity;
}