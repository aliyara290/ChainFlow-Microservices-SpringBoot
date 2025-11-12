package com.aliyara.customerservice.service.interfaces;

import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.dto.response.CustomerResponseDTO;
import com.aliyara.customerservice.payload.ApiResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO create(CustomerRequestDTO requestDTO);
    CustomerResponseDTO update(String id, CustomerRequestDTO requestDTO);
    ApiResponse<Void> delete(String id);
    CustomerResponseDTO findById(String id);
    List<CustomerResponseDTO> findAll();
}