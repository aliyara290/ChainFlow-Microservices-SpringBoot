package com.aliyara.productionservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Production time is required")
    @Positive(message = "Production time must be a positive number")
    private Integer productionTime;

    @PositiveOrZero(message = "Price cannot be negative")
    private double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
