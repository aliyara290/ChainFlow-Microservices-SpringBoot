package com.aliyara.customerservice.dto.request;

import com.aliyara.customerservice.model.enums.DeliveryStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDTO {
    @NotNull(message = "Delivery status is required")
    private DeliveryStatus status;

    @NotBlank(message = "Delivery date is required")
    private String date;

    @NotNull(message = "Cost is required")
    private Double cost;

    @NotNull(message = "Delivery address is required")
    @Valid
    private AdresseRequestDTO adresse2;

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Order ID is required")
    private String orderId;
}