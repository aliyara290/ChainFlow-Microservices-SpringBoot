package com.aliyara.productionservice.dto.request;


import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ProductionOrderRequestDTO {
    @NotBlank(message = "Quantity is required")
    private String quantity;

    @NotNull(message = "Status cannot be null")
    private ProductionOrderStatus status;

    @NotBlank(message = "Start date is required")
    private String startDate;

    @NotBlank(message = "End date is required")
    private String endDate;

    @NotBlank(message = "Product ID is required")
    private String productId;
}
