package com.aliyara.customerservice.controller;

import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.dto.response.CustomerResponseDTO;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.service.interfaces.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> createCustomer(
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO savedCustomer = customerService.create(requestDTO);
        ApiResponse<CustomerResponseDTO> response = new ApiResponse<>(
                true,
                "Customer created successfully",
                savedCustomer
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO updatedCustomer = customerService.update(id, requestDTO);
        ApiResponse<CustomerResponseDTO> response = new ApiResponse<>(
                true,
                "Customer updated successfully",
                updatedCustomer
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(
            @PathVariable String id) {
        CustomerResponseDTO customer = customerService.findById(id);
        ApiResponse<CustomerResponseDTO> response = new ApiResponse<>(
                true,
                "Customer found",
                customer
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.findAll();
        ApiResponse<List<CustomerResponseDTO>> response = new ApiResponse<>(
                true,
                "Customers list retrieved successfully",
                customers
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String id) {
        ApiResponse<Void> response = customerService.delete(id);
        return ResponseEntity.ok(response);
    }
}