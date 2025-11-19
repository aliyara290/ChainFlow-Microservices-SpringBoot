package com.aliyara.supplyservice.service;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.exception.MaterialNotFoundException;
import com.aliyara.supplyservice.exception.NoMaterialsFoundException;
import com.aliyara.supplyservice.mapper.MaterialMapper;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.service.impl.MaterialServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceImplTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MaterialMapper materialMapper;

    @InjectMocks
    private MaterialServiceImpl materialService;

    private Material material;
    private MaterialRequestDTO requestDTO;
    private MaterialResponseDTO responseDTO;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Ali")
                .lastName("Yara")
                .phone("0643569435")
                .email("ali@gmail.com")
                .build();

        material = Material.builder()
                .id(UUID.randomUUID().toString())
                .name("RAM")
                .stock(50)
                .stockMin(20)
                .unit("kg")
                .supplier(supplier)
                .orderMaterials(new ArrayList<>())
                .build();

        requestDTO = MaterialRequestDTO.builder()
                .name("RAM")
                .stock(50)
                .stockMin(20)
                .unit("kg")
                .supplierId(supplier.getId())
                .build();

        responseDTO = MaterialResponseDTO.builder()
                .id(material.getId())
                .name("RAM")
                .stock(50)
                .stockMin(20)
                .unit("kg")
                .supplierId(supplier.getId())
                .build();
    }

    @Test
    @DisplayName("Test create material successfully")
    void testCreateMaterial() {
        when(materialMapper.toEntity(requestDTO)).thenReturn(material);
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        when(materialMapper.toResponse(material)).thenReturn(responseDTO);

        MaterialResponseDTO result = materialService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("RAM");
        assertThat(result.getStock()).isEqualTo(50);
        assertThat(result.getStockMin()).isEqualTo(20);
        assertThat(result.getUnit()).isEqualTo("kg");

        verify(materialMapper, times(1)).toEntity(requestDTO);
        verify(materialRepository, times(1)).save(any(Material.class));
        verify(materialMapper, times(1)).toResponse(material);
    }

    @Test
    @DisplayName("Test update material successfully")
    void testUpdateMaterial() {
        String materialId = material.getId();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        doNothing().when(materialMapper).updateEntityFromDto(requestDTO, material);
        when(materialMapper.toResponse(material)).thenReturn(responseDTO);

        MaterialResponseDTO result = materialService.update(materialId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(materialId);

        verify(materialRepository, times(1)).findById(materialId);
        verify(materialMapper, times(1)).updateEntityFromDto(requestDTO, material);
        verify(materialMapper, times(1)).toResponse(material);
    }

    @Test
    @DisplayName("Test update material throws exception when not found")
    void testUpdateMaterialNotFound() {
        String materialId = UUID.randomUUID().toString();

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(MaterialNotFoundException.class, () -> {
            materialService.update(materialId, requestDTO);
        });

        verify(materialRepository, times(1)).findById(materialId);
    }

    @Test
    @DisplayName("Test delete material successfully")
    void testDeleteMaterial() {
        String materialId = material.getId();

        when(materialRepository.existsById(materialId)).thenReturn(true);
        doNothing().when(materialRepository).deleteById(materialId);

        ApiResponse<Void> result = materialService.delete(materialId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("Material deleted successfully");

        verify(materialRepository, times(1)).existsById(materialId);
        verify(materialRepository, times(1)).deleteById(materialId);
    }

    @Test
    @DisplayName("Test delete material throws exception when not found")
    void testDeleteMaterialNotFound() {
        String materialId = UUID.randomUUID().toString();

        when(materialRepository.existsById(materialId)).thenReturn(false);

        assertThrows(MaterialNotFoundException.class, () -> {
            materialService.delete(materialId);
        });

        verify(materialRepository, times(1)).existsById(materialId);
        verify(materialRepository, never()).deleteById(materialId);
    }

    @Test
    @DisplayName("Test find material by id successfully")
    void testFindById() {
        String materialId = material.getId();

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(materialMapper.toResponse(material)).thenReturn(responseDTO);

        MaterialResponseDTO result = materialService.findById(materialId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(materialId);

        verify(materialRepository, times(1)).findById(materialId);
        verify(materialMapper, times(1)).toResponse(material);
    }

    @Test
    @DisplayName("Test find material by id throws exception when not found")
    void testFindByIdNotFound() {
        String materialId = UUID.randomUUID().toString();

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(MaterialNotFoundException.class, () -> {
            materialService.findById(materialId);
        });

        verify(materialRepository, times(1)).findById(materialId);
    }

    @Test
    @DisplayName("Test find all materials successfully")
    void testFindAll() {
        List<Material> materials = List.of(material);

        when(materialRepository.findAll()).thenReturn(materials);
        when(materialMapper.toResponse(material)).thenReturn(responseDTO);

        List<MaterialResponseDTO> result = materialService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("RAM");

        verify(materialRepository, times(1)).findAll();
        verify(materialMapper, times(1)).toResponse(material);
    }

    @Test
    @DisplayName("Test find all materials throws exception when empty")
    void testFindAllEmpty() {
        when(materialRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoMaterialsFoundException.class, () -> {
            materialService.findAll();
        });

        verify(materialRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test increase material stock")
    void testIncreaseMaterialStock() {
        String materialId = material.getId();
        int quantity = 10;

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        materialService.increaseMaterialStock(materialId, quantity);

        assertThat(material.getStock()).isEqualTo(60);
        verify(materialRepository, times(1)).findById(materialId);
    }

    @Test
    @DisplayName("Test decrease material stock")
    void testDecreaseMaterialStock() {
        String materialId = material.getId();
        int quantity = 10;

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        materialService.decreaseMaterialStock(materialId, quantity);

        assertThat(material.getStock()).isEqualTo(40);
        verify(materialRepository, times(1)).findById(materialId);
    }
}