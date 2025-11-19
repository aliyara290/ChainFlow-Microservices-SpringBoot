package com.aliyara.customerservice.dto.response;

import com.aliyara.customerservice.model.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private String plateNumber;
    private String model;
    private String color;
    private String brand;
    private Double capacity;
    private VehicleStatus status;
    private Integer modelYear;
}