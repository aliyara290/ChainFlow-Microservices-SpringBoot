package com.aliyara.supplyservice.controller;


import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.service.interfaces.OrderService;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateOrder(@PathVariable String id, @PathParam("status") String status) {
        orderService.updateOrderStatus(id, status);
        ApiResponse<Void> response = new ApiResponse<>(true, "Order updated successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orders =  orderService.findAll();
        ApiResponse<List<OrderResponseDTO>> response = new ApiResponse<>(true, "Orders found", orders);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
