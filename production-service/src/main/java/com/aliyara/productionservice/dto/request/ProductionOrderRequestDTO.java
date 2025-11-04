package com.aliyara.productionservice.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ProductionOrderRequestDTO {
    private String id;
    private String quantity;
    private String startDate;
    private String endDate;
}
