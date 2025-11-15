package com.aliyara.customerservice.service.impl;

import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.dto.response.CustomerResponseDTO;
import com.aliyara.customerservice.exception.FailedToInsertDataException;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.mapper.CustomerMapper;
import com.aliyara.customerservice.model.Customer;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.repository.CustomerRepository;
import com.aliyara.customerservice.service.interfaces.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO requestDTO) {
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new FailedToInsertDataException("Email already exists: " + requestDTO.getEmail());
        }

        if (customerRepository.existsByPhone(requestDTO.getPhone())) {
            throw new FailedToInsertDataException("Phone number already exists: " + requestDTO.getPhone());
        }

        try {
            Customer customer = customerMapper.toEntity(requestDTO);
            Customer savedCustomer = customerRepository.save(customer);
            return customerMapper.toResponse(savedCustomer);
        } catch (Exception e) {
            throw new FailedToInsertDataException("Failed to create customer: " + e.getMessage());
        }
    }

    @Override
    public CustomerResponseDTO update(String id, CustomerRequestDTO requestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + id));

        if (!existingCustomer.getEmail().equals(requestDTO.getEmail())
                && customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new FailedToInsertDataException("Email already exists: " + requestDTO.getEmail());
        }

        if (!existingCustomer.getPhone().equals(requestDTO.getPhone())
                && customerRepository.existsByPhone(requestDTO.getPhone())) {
            throw new FailedToInsertDataException("Phone number already exists: " + requestDTO.getPhone());
        }

        try {
            customerMapper.updateEntityFromDTO(requestDTO, existingCustomer);
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return customerMapper.toResponse(updatedCustomer);
        } catch (Exception e) {
            throw new FailedToInsertDataException("Failed to update customer: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!customerRepository.existsById(id)) {
            throw new RecordNotFoundException("Customer not found with id: " + id);
        }

        try {
            customerRepository.deleteById(id);
            return new ApiResponse<>(true, "Customer deleted successfully", null);
        } catch (Exception e) {
            throw new FailedToInsertDataException("Failed to delete customer: " + e.getMessage());
        }
    }

    @Override
    public CustomerResponseDTO findById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponseDTO> findAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            throw new NoRecordFoundException("No customers found");
        }
        return customers.stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

}