package com.aliyara.productionservice.service.impl;

import com.aliyara.productionservice.client.MaterialFeignClient;
import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.request.MaterialsDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.exception.FailedToSaveDataException;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.BOMRepository;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.service.interfaces.BOMService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class BOMServiceImpl implements BOMService {

    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;
    private final MaterialFeignClient materialFeignClient;

    @Override
    public ApiResponse<Void> create(BOMRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product"));

        for (MaterialsDTO materialDTO : requestDTO.getMaterials()) {
            try {
                MaterialDTO materialResponse = materialFeignClient.getMaterialById(materialDTO.getMaterialId());

                if (materialResponse == null) {
                    throw new RecordNotFoundException("Material with ID: " + materialDTO.getMaterialId());
                }

                BOM bom = BOM.builder()
                        .materialId(materialDTO.getMaterialId())
                        .quantity(materialDTO.getQuantity())
                        .product(product)
                        .build();

                bomRepository.save(bom);
            } catch (Exception e) {
                throw new FailedToSaveDataException("Failed to create BOM: " + e.getMessage());
            }
        }

        return new ApiResponse<>(true, "BOM created successfully", null);
    }

    @Override
    public BOMResponseDTO update(String id, BOMRequestDTO requestDTO) {
        BOM existingBom = bomRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new RecordNotFoundException("BOM"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product"));

        existingBom.setProduct(product);
        BOM updatedBom = bomRepository.save(existingBom);

        return mapToResponse(updatedBom);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!bomRepository.existsById(Integer.parseInt(id))) {
            throw new RecordNotFoundException("BOM");
        }
        bomRepository.deleteById(Integer.parseInt(id));
        return new ApiResponse<>(true, "BOM deleted successfully", null);
    }

    @Override
    public BOMResponseDTO findById(String id) {
        BOM bom = bomRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new RecordNotFoundException("BOM"));
        return mapToResponse(bom);
    }

    @Override
    public List<BOMResponseDTO> findAll() {
        List<BOM> boms = bomRepository.findAll();
        return boms.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private BOMResponseDTO mapToResponse(BOM bom) {
        BOMResponseDTO response = new BOMResponseDTO();
        // Map fields as needed
        return response;
    }
}