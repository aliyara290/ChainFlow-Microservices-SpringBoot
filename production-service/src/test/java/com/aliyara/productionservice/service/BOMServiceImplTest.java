package com.aliyara.productionservice.service;

import com.aliyara.productionservice.client.MaterialFeignClient;
import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.request.MaterialsDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.exception.FailedToSaveDataException;
import com.aliyara.productionservice.exception.InsufficientStockException;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.BOMRepository;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.service.impl.BOMServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BOMServiceImplTest {

    @Mock
    private BOMRepository bomRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MaterialFeignClient materialFeignClient;

    @InjectMocks
    private BOMServiceImpl bomService;

    private Product product;
    private BOM bom;
    private MaterialsDTO materialsDTO;
    private MaterialDTO materialDTO;
    private BOMRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName("Laptop");
        product.setProductionTime(120);
        product.setPrice(1200.50);
        product.setStock(50);

        String materialId = UUID.randomUUID().toString();

        materialDTO = MaterialDTO.builder()
                .id(materialId)
                .name("RAM")
                .stock(100)
                .stockMin(20)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(materialId);
        materialsDTO.setQuantity(10);

        bom = BOM.builder()
                .id(UUID.randomUUID().toString())
                .materialId(materialId)
                .quantity(10)
                .product(product)
                .build();

        requestDTO = new BOMRequestDTO();
        requestDTO.setProductId(product.getId());
        requestDTO.setMaterials(List.of(materialsDTO));
    }

    @Test
    @DisplayName("Test create BOM successfully")
    void testCreateBOM() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId())).thenReturn(materialDTO);
        when(bomRepository.saveAll(anyList())).thenReturn(List.of(bom));

        ApiResponse<Void> result = bomService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("BOM created successfully");

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(1)).getMaterialById(materialsDTO.getMaterialId());
        verify(bomRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Test create BOM throws exception when product not found")
    void testCreateBOMProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            bomService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, never()).getMaterialById(any());
        verify(bomRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Test create BOM throws exception when material not found")
    void testCreateBOMMaterialNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId()))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThrows(RecordNotFoundException.class, () -> {
            bomService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(1)).getMaterialById(materialsDTO.getMaterialId());
        verify(bomRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Test create BOM throws exception when insufficient stock")
    void testCreateBOMInsufficientStock() {
        MaterialDTO lowStockMaterial = MaterialDTO.builder()
                .id(materialsDTO.getMaterialId())
                .name("RAM")
                .stock(5)  // Less than required quantity (10)
                .stockMin(20)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId())).thenReturn(lowStockMaterial);

        assertThrows(InsufficientStockException.class, () -> {
            bomService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(1)).getMaterialById(materialsDTO.getMaterialId());
        verify(bomRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Test create BOM throws exception when save fails")
    void testCreateBOMSaveFails() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId())).thenReturn(materialDTO);
        when(bomRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        assertThrows(FailedToSaveDataException.class, () -> {
            bomService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(1)).getMaterialById(materialsDTO.getMaterialId());
        verify(bomRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Test create BOM with multiple materials successfully")
    void testCreateBOMMultipleMaterials() {
        String material2Id = UUID.randomUUID().toString();

        MaterialsDTO materialsDTO2 = new MaterialsDTO();
        materialsDTO2.setMaterialId(material2Id);
        materialsDTO2.setQuantity(5);

        MaterialDTO materialDTO2 = MaterialDTO.builder()
                .id(material2Id)
                .name("SSD")
                .stock(50)
                .stockMin(10)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        BOMRequestDTO multiMaterialRequest = new BOMRequestDTO();
        multiMaterialRequest.setProductId(product.getId());
        multiMaterialRequest.setMaterials(List.of(materialsDTO, materialsDTO2));

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId())).thenReturn(materialDTO);
        when(materialFeignClient.getMaterialById(material2Id)).thenReturn(materialDTO2);
        when(bomRepository.saveAll(anyList())).thenReturn(List.of(bom));

        ApiResponse<Void> result = bomService.create(multiMaterialRequest);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(2)).getMaterialById(any());
        verify(bomRepository, times(1)).saveAll(anyList());
    }

//    @Test
//    @DisplayName("Test update BOM successfully")
//    void testUpdateBOM() {
//        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";
//
//        when(bomRepository.findById(bomId)).thenReturn(Optional.of(bom));
//        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
//        when(bomRepository.save(any(BOM.class))).thenReturn(bom);
//
//        BOMResponseDTO result = bomService.update(bomId.toString(), requestDTO);
//
//        assertThat(result).isNotNull();
//
//        verify(bomRepository, times(1)).findById(bomId);
//        verify(productRepository, times(1)).findById(product.getId());
//        verify(bomRepository, times(1)).save(any(BOM.class));
//    }

    @Test
    @DisplayName("Test update BOM throws exception when BOM not found")
    void testUpdateBOMNotFound() {
        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";

        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            bomService.update(bomId.toString(), requestDTO);
        });

        verify(bomRepository, times(1)).findById(bomId);
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Test delete BOM successfully")
    void testDeleteBOM() {
        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";

        when(bomRepository.existsById(bomId)).thenReturn(true);
        doNothing().when(bomRepository).deleteById(bomId);

        ApiResponse<Void> result = bomService.delete(bomId.toString());

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("BOM deleted successfully");

        verify(bomRepository, times(1)).existsById(bomId);
        verify(bomRepository, times(1)).deleteById(bomId);
    }

    @Test
    @DisplayName("Test delete BOM throws exception when not found")
    void testDeleteBOMNotFound() {
        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";

        when(bomRepository.existsById(bomId)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            bomService.delete(bomId.toString());
        });

        verify(bomRepository, times(1)).existsById(bomId);
        verify(bomRepository, never()).deleteById(bomId);
    }

//    @Test
//    @DisplayName("Test find BOM by id successfully")
//    void testFindById() {
////        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";
//
//        when(bomRepository.findById(bom.getId())).thenReturn(Optional.of(bom));
//
//        BOMResponseDTO result = bomService.findById(bom.getId());
//
//        assertThat(result).isNotNull();
//
//        verify(bomRepository, times(1)).findById(bom.getId());
//    }

    @Test
    @DisplayName("Test find BOM by id throws exception when not found")
    void testFindByIdNotFound() {
        String bomId = "ad1da7fb-5ed8-48e3-8efe-c38ba426efg5";

        when(bomRepository.findById(bomId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            bomService.findById(bomId);
        });

        verify(bomRepository, times(1)).findById(bomId);
    }

//    @Test
//    @DisplayName("Test find all BOMs successfully")
//    void testFindAll() {
//        List<BOM> boms = List.of(bom);
//
//        when(bomRepository.findAll()).thenReturn(boms);
//
//        List<BOMResponseDTO> result = bomService.findAll();
//
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//
//        verify(bomRepository, times(1)).findAll();
//    }

    @Test
    @DisplayName("Test create BOM handles Feign client error gracefully")
    void testCreateBOMFeignClientError() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(materialFeignClient.getMaterialById(materialsDTO.getMaterialId()))
                .thenThrow(mock(FeignException.class));

        assertThrows(RuntimeException.class, () -> {
            bomService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(materialFeignClient, times(1)).getMaterialById(materialsDTO.getMaterialId());
        verify(bomRepository, never()).saveAll(anyList());
    }
}