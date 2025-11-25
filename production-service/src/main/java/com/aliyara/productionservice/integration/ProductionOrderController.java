package com.aliyara.productionservice.integration;

import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.service.interfaces.ProductionOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/production-orders")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductionOrderResponseDTO>> create(@Valid @RequestBody ProductionOrderRequestDTO requestDTO) {
        ProductionOrderResponseDTO savedOrder = productionOrderService.create(requestDTO);
        ApiResponse<ProductionOrderResponseDTO> response = new ApiResponse<>(true, "Production order created successfully", savedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionOrderResponseDTO>> update(@PathVariable String id, @Valid @RequestBody ProductionOrderRequestDTO requestDTO) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.update(id, requestDTO);
        ApiResponse<ProductionOrderResponseDTO> response = new ApiResponse<>(true, "Production order updated successfully", updatedOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductionOrderResponseDTO>> getById(@PathVariable String id) {
        ProductionOrderResponseDTO order = productionOrderService.findById(id);
        ApiResponse<ProductionOrderResponseDTO> response = new ApiResponse<>(true, "Production order found!", order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductionOrderResponseDTO>>> getAll() {
        List<ProductionOrderResponseDTO> orders = productionOrderService.findAll();
        ApiResponse<List<ProductionOrderResponseDTO>> response = new ApiResponse<>(true, "Production orders list", orders);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = productionOrderService.delete(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<ApiResponse<Void>> updateInProduction(@PathVariable String id) {
        productionOrderService.onProduction(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Yes", null);
        return ResponseEntity.ok(response);
    }
}