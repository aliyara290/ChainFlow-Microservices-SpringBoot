package com.aliyara.customerservice.controller;


import com.aliyara.customerservice.dto.request.DeliveryRequestDTO;
import com.aliyara.customerservice.dto.response.DeliveryResponseDTO;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.service.interfaces.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryResponseDTO>> createDelivery(@Valid @RequestBody DeliveryRequestDTO deliveryRequestDTO) {
        DeliveryResponseDTO deliveryResponseDTO = deliveryService.createDelivery(deliveryRequestDTO);
        ApiResponse<DeliveryResponseDTO> response = new ApiResponse<>(true, "Delivery created successfully", deliveryResponseDTO);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryResponseDTO>> updateDelivery(@Valid @RequestBody DeliveryRequestDTO deliveryRequestDTO, @PathVariable String id) {
        DeliveryResponseDTO deliveryResponseDTO = deliveryService.updateDelivery(deliveryRequestDTO, id);
        ApiResponse<DeliveryResponseDTO> response = new ApiResponse<>(true, "Delivery updated successfully", deliveryResponseDTO);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryResponseDTO>> getDelivery(@PathVariable String id) {
        DeliveryResponseDTO deliveryResponseDTO = deliveryService.getDelivery(id);
        ApiResponse<DeliveryResponseDTO> response = new ApiResponse<>(true, "Delivery created successfully", deliveryResponseDTO);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryResponseDTO>>> getDeliveries() {
        List<DeliveryResponseDTO> deliveryResponseDTOs = deliveryService.getDeliveries();
        ApiResponse<List<DeliveryResponseDTO>> response = new ApiResponse<>(true, "Deliveries found", deliveryResponseDTOs);
        return ResponseEntity.ok().body(response);
    }
}
