package com.aliyara.customerservice.dto.response;

import com.aliyara.customerservice.model.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {
    private String id;
    private DeliveryStatus status;
    private LocalDate date;
    private Double cost;
    private AdresseResponseDTO adresse;
    private VehicleResponseDTO vehicle;
    private DriverResponseDTO driver;
    private OrderResponseDTO order;
}