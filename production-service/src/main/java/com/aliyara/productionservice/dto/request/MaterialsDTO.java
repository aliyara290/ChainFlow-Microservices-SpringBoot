package com.aliyara.productionservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaterialsDTO {
    @NotBlank(message = "Material ID is required")
    private String materialId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;
}