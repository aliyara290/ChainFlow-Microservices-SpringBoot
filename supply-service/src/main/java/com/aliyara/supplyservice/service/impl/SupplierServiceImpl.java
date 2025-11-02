package com.aliyara.supplyservice.service.impl;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.exception.NoSupplierFoundException;
import com.aliyara.supplyservice.exception.SupplierNotFoundException;
import com.aliyara.supplyservice.mapper.SupplierMapper;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.aliyara.supplyservice.service.interfaces.SupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponseDTO create(SupplierRequestDTO requestDTO) {
        Supplier supplierToEntity = supplierMapper.toEntity(requestDTO);
        Supplier savedSupplier = supplierRepository.save(supplierToEntity);
        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponseDTO update(String supplierId, SupplierRequestDTO requestDTO) {
       Supplier existingSupplier = supplierRepository.findById(supplierId)
               .orElseThrow(() -> new RuntimeException("Supplier not found!!"));
       supplierMapper.updateEntityFromDto(requestDTO, existingSupplier);
       Supplier updateSupplier = supplierRepository.save(existingSupplier);
       return supplierMapper.toResponse(updateSupplier);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if(!supplierRepository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        supplierRepository.deleteById(id);
        return new ApiResponse<>(true, "Supplier deleted successfully", null);
    }

    @Override
    public SupplierResponseDTO findById(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
        return supplierMapper.toResponse(supplier);
    }

    @Override
    public List<SupplierResponseDTO> findAll() {
        List<Supplier> suppliers = supplierRepository.findAll();
        if(suppliers.isEmpty()) {
            throw new NoSupplierFoundException();
        }
        return suppliers.stream().map(supplierMapper::toResponse).collect(Collectors.toList());
    }
}
