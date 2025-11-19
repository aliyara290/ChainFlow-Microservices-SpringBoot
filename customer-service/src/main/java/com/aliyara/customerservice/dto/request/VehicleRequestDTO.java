package com.aliyara.customerservice.dto.request;

import com.aliyara.customerservice.model.enums.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
    private String plateNumber;
    private String model;
    private String color;
    private String brand;
    private Double capacity;
    private VehicleStatus status;
    private Integer modelYear;
}