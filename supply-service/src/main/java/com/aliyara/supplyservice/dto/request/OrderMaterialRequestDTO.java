package com.aliyara.supplyservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMaterialRequestDTO {
    private String materialId;
    private Integer quantity;
}