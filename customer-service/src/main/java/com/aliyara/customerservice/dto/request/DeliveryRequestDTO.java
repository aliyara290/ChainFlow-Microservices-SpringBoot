package com.aliyara.customerservice.dto.request;

import com.aliyara.customerservice.model.enums.DeliveryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDTO {
    private DeliveryStatus status;
    private LocalDate date;
    private Double cost;
    private AdresseRequestDTO adresse;
    private String vehicleId;
    private String driverId;
    private String orderId;
}