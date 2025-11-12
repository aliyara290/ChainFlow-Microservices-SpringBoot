package com.aliyara.customerservice.dto.request;

import com.aliyara.customerservice.model.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Capacity is required")
    private Double capacity;

    @NotNull(message = "Vehicle status is required")
    private VehicleStatus status;

    @NotNull(message = "Year is required")
    private Integer year;
}