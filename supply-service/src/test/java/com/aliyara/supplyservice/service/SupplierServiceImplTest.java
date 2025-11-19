package com.aliyara.supplyservice.service;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.exception.NoSupplierFoundException;
import com.aliyara.supplyservice.exception.SupplierNotFoundException;
import com.aliyara.supplyservice.mapper.SupplierMapper;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.aliyara.supplyservice.service.impl.SupplierServiceImpl;
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
public class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private SupplierRequestDTO requestDTO;
    private SupplierResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        requestDTO = SupplierRequestDTO.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        responseDTO = SupplierResponseDTO.builder()
                .id(supplier.getId())
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();
    }

    @Test
    @DisplayName("Test create supplier successfully")
    void testCreateSupplier() {
        when(supplierMapper.toEntity(requestDTO)).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ali");
        assertThat(result.getLastName()).isEqualTo("Yara");
        assertThat(result.getEmail()).isEqualTo("ali@gmail.com");
        assertThat(result.getRating()).isEqualTo(5.6);

        verify(supplierMapper, times(1)).toEntity(requestDTO);
        verify(supplierRepository, times(1)).save(any(Supplier.class));
        verify(supplierMapper, times(1)).toResponse(supplier);
    }

    @Test
    @DisplayName("Test update supplier successfully")
    void testUpdateSupplier() {
        String supplierId = supplier.getId();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierMapper).updateEntityFromDto(requestDTO, supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toResponse(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.update(supplierId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(supplierId);

        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierMapper, times(1)).updateEntityFromDto(requestDTO, supplier);
        verify(supplierRepository, times(1)).save(any(Supplier.class));
        verify(supplierMapper, times(1)).toResponse(supplier);
    }

    @Test
    @DisplayName("Test update supplier throws exception when not found")
    void testUpdateSupplierNotFound() {
        String supplierId = UUID.randomUUID().toString();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            supplierService.update(supplierId, requestDTO);
        });

        verify(supplierRepository, times(1)).findById(supplierId);
    }

    @Test
    @DisplayName("Test delete supplier successfully")
    void testDeleteSupplier() {
        String supplierId = supplier.getId();

        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(supplierId);

        ApiResponse<Void> result = supplierService.delete(supplierId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("Supplier deleted successfully");

        verify(supplierRepository, times(1)).existsById(supplierId);
        verify(supplierRepository, times(1)).deleteById(supplierId);
    }

    @Test
    @DisplayName("Test delete supplier throws exception when not found")
    void testDeleteSupplierNotFound() {
        String supplierId = UUID.randomUUID().toString();

        when(supplierRepository.existsById(supplierId)).thenReturn(false);

        assertThrows(SupplierNotFoundException.class, () -> {
            supplierService.delete(supplierId);
        });

        verify(supplierRepository, times(1)).existsById(supplierId);
        verify(supplierRepository, never()).deleteById(supplierId);
    }

    @Test
    @DisplayName("Test find supplier by id successfully")
    void testFindById() {
        String supplierId = supplier.getId();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponse(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.findById(supplierId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(supplierId);
        assertThat(result.getFirstName()).isEqualTo("Ali");

        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierMapper, times(1)).toResponse(supplier);
    }

    @Test
    @DisplayName("Test find supplier by id throws exception when not found")
    void testFindByIdNotFound() {
        String supplierId = UUID.randomUUID().toString();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> {
            supplierService.findById(supplierId);
        });

        verify(supplierRepository, times(1)).findById(supplierId);
    }

    @Test
    @DisplayName("Test find all suppliers successfully")
    void testFindAll() {
        List<Supplier> suppliers = List.of(supplier);

        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(supplierMapper.toResponse(supplier)).thenReturn(responseDTO);

        List<SupplierResponseDTO> result = supplierService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Ali");

        verify(supplierRepository, times(1)).findAll();
        verify(supplierMapper, times(1)).toResponse(supplier);
    }

    @Test
    @DisplayName("Test find all suppliers throws exception when empty")
    void testFindAllEmpty() {
        when(supplierRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoSupplierFoundException.class, () -> {
            supplierService.findAll();
        });

        verify(supplierRepository, times(1)).findAll();
    }
}