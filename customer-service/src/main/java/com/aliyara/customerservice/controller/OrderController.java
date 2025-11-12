package com.aliyara.customerservice.controller;

import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.dto.response.OrderResponseDTO;
import com.aliyara.customerservice.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CREATE ORDER
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody OrderRequestDTO requestDTO) {
        OrderResponseDTO savedOrder = orderService.create(requestDTO);
        ApiResponse<OrderResponseDTO> response =
                new ApiResponse<>(true, "Order created successfully", savedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL ORDERS
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.findAll();
        ApiResponse<List<OrderResponseDTO>> response =
                new ApiResponse<>(true, "Orders retrieved successfully", orders);
        return ResponseEntity.ok(response);
    }

    // GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable String id) {
        OrderResponseDTO order = orderService.findById(id);
        ApiResponse<OrderResponseDTO> response =
                new ApiResponse<>(true, "Order retrieved successfully", order);
        return ResponseEntity.ok(response);
    }

    // UPDATE ORDER
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrder(
            @PathVariable String id,
            @RequestBody OrderRequestDTO requestDTO
    ) {
        OrderResponseDTO updatedOrder = orderService.update(id, requestDTO);
        ApiResponse<OrderResponseDTO> response =
                new ApiResponse<>(true, "Order updated successfully", updatedOrder);
        return ResponseEntity.ok(response);
    }

    // DELETE ORDER
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        orderService.delete(id);
        ApiResponse<Void> response =
                new ApiResponse<>(true, "Order deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
