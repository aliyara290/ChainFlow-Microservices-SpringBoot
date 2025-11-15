package com.aliyara.supplyservice.service;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderMaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.dto.response.OrderMaterialResponseDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.mapper.MaterialMapper;
import com.aliyara.supplyservice.mapper.OrderMapper;
import com.aliyara.supplyservice.mapper.OrderMaterialMapper;
import com.aliyara.supplyservice.mapper.SupplierMapper;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Order;
import com.aliyara.supplyservice.model.OrderMaterial;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.model.enums.OrderStatus;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.repository.OrderRepository;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.aliyara.supplyservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Supplier supplier;

    private Material material;

    private OrderMaterialRequestDTO orderMaterialRequestDTO;
    private OrderMaterial orderMaterial;
    private OrderMaterialResponseDTO orderMaterialResponseDTO;

    private OrderRequestDTO orderRequestDTO;
    private Order order;
    private OrderResponseDTO orderResponseDTO;

    @BeforeAll
    static void setupAll() {
        System.out.println("Starting test class OrderServiceTest");
    }

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Ali")
                .lastName("Yara")
                .phone("0643569435")
                .email("ali@gmail.com")
                .rating(5.7)
                .leadTime(10)
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

        orderMaterialRequestDTO = OrderMaterialRequestDTO.builder()
                .materialId(material.getId())
                .quantity(10)
                .build();

        orderMaterialResponseDTO = OrderMaterialResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .materialId(material.getId())
                .quantity(10)
                .materialName(material.getName())
                .build();

        orderRequestDTO = OrderRequestDTO.builder()
                .supplierId(supplier.getId())
                .materials(List.of(orderMaterialRequestDTO))
                .build();

        order = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderDate(LocalDate.now())
                .supplierId(supplier.getId())
                .status(OrderStatus.PENDING)
                .orderMaterials(new ArrayList<>())
                .build();

        orderMaterial = OrderMaterial.builder()
                .id(UUID.randomUUID().toString())
                .material(material)
                .quantity(10)
                .order(order)
                .build();

        order.getOrderMaterials().add(orderMaterial);

        orderResponseDTO = OrderResponseDTO.builder()
                .id(order.getId())
                .orderDate(LocalDate.now())
                .supplierId(supplier.getId())
                .status(order.getStatus().name())
                .orderMaterials(List.of(orderMaterialResponseDTO))
                .build();
    }

    @Test
    @DisplayName("Test the creation of an order")
    void testCreateOrder() {
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(materialRepository.findById(material.getId())).thenReturn(Optional.of(material));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.create(orderRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getSupplierId()).isEqualTo(supplier.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING.name());
        assertThat(result.getOrderMaterials()).isNotNull();
        assertThat(result.getOrderMaterials()).hasSize(1);
        assertThat(result.getOrderMaterials().get(0).getMaterialId()).isEqualTo(material.getId());
        assertThat(result.getOrderMaterials().get(0).getQuantity()).isEqualTo(10);

        verify(supplierRepository, times(1)).findById(supplier.getId());
        verify(materialRepository, times(1)).findById(material.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toResponse(any(Order.class));
    }
}