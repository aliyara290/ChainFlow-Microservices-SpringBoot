package com.aliyara.supplyservice.service.impl;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.exception.MaterialNotFoundException;
import com.aliyara.supplyservice.exception.NoMaterialsFoundException;
import com.aliyara.supplyservice.exception.NoSupplierFoundException;
import com.aliyara.supplyservice.exception.SupplierNotFoundException;
import com.aliyara.supplyservice.mapper.MaterialMapper;
import com.aliyara.supplyservice.mapper.SupplierMapper;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.service.interfaces.MaterialService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Override
    public MaterialResponseDTO create(MaterialRequestDTO requestDTO) {
        Material materialToEntity = materialMapper.toEntity(requestDTO);
        Material savedMaterial = materialRepository.save(materialToEntity);
        return materialMapper.toResponse(savedMaterial);
    }

    @Override
    public MaterialResponseDTO update(String id, MaterialRequestDTO requestDTO) {
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));
        materialMapper.updateEntityFromDto(requestDTO, existingMaterial);
        return materialMapper.toResponse(existingMaterial);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if(!materialRepository.existsById(id)) {
            throw new MaterialNotFoundException(id);
        }
        materialRepository.deleteById(id);
        return new ApiResponse<>(true, "Material deleted successfully", null);
    }

    @Override
    public MaterialResponseDTO findById(String id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));
        return materialMapper.toResponse(material);
    }

    @Override
    public List<MaterialResponseDTO> findAll() {
        List<Material> materials = materialRepository.findAll();
        if(materials.isEmpty()) {
            throw new NoMaterialsFoundException();
        }
        return materials.stream().map(materialMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void increaseMaterialStock(String materialId, int quantity) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialNotFoundException(materialId));
        material.setStock(material.getStock() + quantity);
    }

    @Override
    public void decreaseMaterialStock(String materialId, int quantity) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialNotFoundException(materialId));
        material.setStock(material.getStock() - quantity);
    }
}
