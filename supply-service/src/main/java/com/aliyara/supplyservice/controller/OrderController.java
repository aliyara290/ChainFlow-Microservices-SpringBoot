package com.aliyara.supplyservice.controller;


import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody OrderRequestDTO requestDTO) {
        OrderResponseDTO savedOrder = orderService.create(requestDTO);
        ApiResponse<OrderResponseDTO> response = new ApiResponse<>(true, "Order created successfully", savedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
