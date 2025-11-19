package com.aliyara.productionservice.service.impl;

import com.aliyara.productionservice.client.MaterialFeignClient;
import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.request.MaterialsDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.exception.FailedToSaveDataException;
import com.aliyara.productionservice.exception.InsufficientStockException;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.mapper.BOMMapper;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.BOMRepository;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.service.interfaces.BOMService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BOMServiceImpl implements BOMService {

    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;
    private final MaterialFeignClient materialFeignClient;
    private final BOMMapper bomMapper;

    @Override
    public ApiResponse<Void> create(BOMRequestDTO requestDTO) {

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product with ID: " + requestDTO.getProductId()));

        List<MaterialDTO> validatedMaterials = validateMaterials(requestDTO.getMaterials());

        List<BOM> bomsToSave = new ArrayList<>();
        for (int i = 0; i < requestDTO.getMaterials().size(); i++) {
            MaterialsDTO materialDTO = requestDTO.getMaterials().get(i);

            BOM bom = BOM.builder()
                    .materialId(materialDTO.getMaterialId())
                    .quantity(materialDTO.getQuantity())
                    .product(product)
                    .build();

            bomsToSave.add(bom);
        }

        try {
            bomRepository.saveAll(bomsToSave);
            log.info("Successfully created {} BOM entries for product {}",
                    bomsToSave.size(), product.getId());
            return new ApiResponse<>(true, "BOM created successfully", null);

        } catch (Exception e) {
            log.error("Failed to save BOM entries: {}", e.getMessage());
            throw new FailedToSaveDataException("Failed to save BOM entries to database");
        }
    }

    private List<MaterialDTO> validateMaterials(List<MaterialsDTO> materials) {
        List<MaterialDTO> validatedMaterials = new ArrayList<>();

        for (MaterialsDTO materialDTO : materials) {
            try {
                MaterialDTO materialResponse = materialFeignClient.getMaterialById(materialDTO.getMaterialId());

                if (materialResponse == null) {
                    throw new RecordNotFoundException("Material with ID: " + materialDTO.getMaterialId());
                }

                log.info("Validating material: {} (Stock: {}, Required: {})",
                        materialResponse.getName(),
                        materialResponse.getStock(),
                        materialDTO.getQuantity());

                if (materialResponse.getStock() < materialDTO.getQuantity()) {
                    throw new InsufficientStockException(
                            "Material '" + materialResponse.getName() +
                                    "' has insufficient stock. Available: " + materialResponse.getStock() +
                                    ", Required: " + materialDTO.getQuantity()
                    );
                }
                validatedMaterials.add(materialResponse);

            } catch (FeignException.NotFound e) {
                log.error("Material not found: {}", materialDTO.getMaterialId());
                throw new RecordNotFoundException("Material with ID: " + materialDTO.getMaterialId());

            } catch (FeignException e) {
                log.error("Error calling material service: {}", e.getMessage());
                throw new RuntimeException("Failed to validate material: " + materialDTO.getMaterialId() +
                        ". Material service error: " + e.getMessage());
            }
        }

        return validatedMaterials;
    }


    @Override
    public BOMResponseDTO update(String id, BOMRequestDTO requestDTO) {
        BOM existingBom = bomRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("BOM"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product"));

        bomMapper.updateEntityFomDTO(requestDTO, existingBom);
        BOM updatedBom = bomRepository.save(existingBom);

        return bomMapper.toResponse(updatedBom);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!bomRepository.existsById(id)) {
            throw new RecordNotFoundException("BOM");
        }
        bomRepository.deleteById(id);
        return new ApiResponse<>(true, "BOM deleted successfully", null);
    }

    @Override
    public BOMResponseDTO findById(String id) {
        BOM bom = bomRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("BOM"));
        return bomMapper.toResponse(bom);
    }

    @Override
    public List<BOMResponseDTO> findAll() {
        List<BOM> boms = bomRepository.findAll();
        return boms.stream().map(bomMapper::toResponse).collect(Collectors.toList());
    }
}