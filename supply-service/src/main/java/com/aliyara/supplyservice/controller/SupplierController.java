package com.aliyara.supplyservice.controller;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.service.interfaces.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponseDTO>>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.findAll();
        ApiResponse<List<SupplierResponseDTO>> response = new ApiResponse<>(true, "Suppliers list", suppliers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDTO>> getSupplier(@PathVariable String id) {
        SupplierResponseDTO supplier = supplierService.findById(id);
        ApiResponse<SupplierResponseDTO> response = new ApiResponse<>(true, "Supplier found!", supplier);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponseDTO>> addSupplier(@RequestBody @Valid SupplierRequestDTO requestDTO) {
        SupplierResponseDTO createdSupplier = supplierService.create(requestDTO);
        ApiResponse<SupplierResponseDTO> response = new ApiResponse<>(true, "Supplier created successfully", createdSupplier);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDTO>> updateSupplier(@PathVariable String id, @RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO updatedSupplier = supplierService.update(id, requestDTO);
        ApiResponse<SupplierResponseDTO> response = new ApiResponse<>(true, "Supplier updated successfully", updatedSupplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable String id) {
        ApiResponse<Void> response = supplierService.delete(id);
        return ResponseEntity.ok(response);
    }
}
