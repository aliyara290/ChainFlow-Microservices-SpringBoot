package com.aliyara.supplyservice.dto.response;

import com.aliyara.supplyservice.model.OrderMaterial;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class OrderResponseDTO {
    private String id;
    private String supplierId;
    private String status;
    private List<OrderMaterial> orderMaterials;
}
