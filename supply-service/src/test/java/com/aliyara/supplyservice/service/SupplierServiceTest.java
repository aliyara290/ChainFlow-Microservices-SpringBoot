package com.aliyara.supplyservice.service;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.mapper.SupplierMapper;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.aliyara.supplyservice.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierServiceImpl;

    @Test
    public void testIsSupplierCreated() {
        SupplierRequestDTO requestDTO = SupplierRequestDTO.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        Supplier supplierEntity = Supplier.builder()
                .id("8d1b69f5-8d37-418b-b491-2faffb6c9c31")
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        SupplierResponseDTO responseDTO = SupplierResponseDTO.builder()
                .id("8d1b69f5-8d37-418b-b491-2faffb6c9c31")
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        when(supplierMapper.toEntity(requestDTO)).thenReturn(supplierEntity);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplierEntity);
        when(supplierMapper.toResponse(supplierEntity)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierServiceImpl.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ali");
        assertThat(result.getLastName()).isEqualTo("Yara");
        assertThat(result.getEmail()).isEqualTo("ali@gmail.com");

        verify(supplierMapper).toEntity(requestDTO);
        verify(supplierRepository).save(any(Supplier.class));
        verify(supplierMapper).toResponse(supplierEntity);
    }
}