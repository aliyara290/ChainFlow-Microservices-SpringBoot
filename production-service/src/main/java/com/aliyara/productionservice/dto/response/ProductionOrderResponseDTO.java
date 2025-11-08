package com.aliyara.productionservice.dto.response;

import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ProductionOrderResponseDTO {
    private String id;
    private String quantity;
    private ProductionOrderStatus status;
    private String productId;
    private String startDate;
    private String endDate;
}
