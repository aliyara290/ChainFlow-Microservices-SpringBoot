package com.aliyara.supplyservice.integration;

import com.aliyara.supplyservice.dto.request.OrderMaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Order;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.model.enums.OrderStatus;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.repository.OrderRepository;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier testSupplier;
    private Material testMaterial;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        materialRepository.deleteAll();
        supplierRepository.deleteAll();

        testSupplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .phone("0643569435")
                .email("ali@gmail.com")
                .rating(5.7)
                .leadTime(10)
                .build();
        testSupplier = supplierRepository.save(testSupplier);

        testMaterial = Material.builder()
                .name("RAM")
                .stock(50)
                .stockMin(20)
                .unit("kg")
                .supplier(testSupplier)
                .orderMaterials(new ArrayList<>())
                .build();
        testMaterial = materialRepository.save(testMaterial);

        testOrder = Order.builder()
                .supplierId(testSupplier.getId())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .orderMaterials(new ArrayList<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        materialRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create order endpoint")
    void testCreateOrder() throws Exception {
        OrderMaterialRequestDTO materialDTO = OrderMaterialRequestDTO.builder()
                .materialId(testMaterial.getId())
                .quantity(10)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .supplierId(testSupplier.getId())
                .materials(List.of(materialDTO))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.supplierId").value(testSupplier.getId()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.orderMaterials", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all orders endpoint")
    void testGetAllOrders() throws Exception {
        testOrder.addMaterial(testMaterial, 10);
        orderRepository.save(testOrder);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Orders found"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Test update order status endpoint")
    void testUpdateOrderStatus() throws Exception {
        testOrder.addMaterial(testMaterial, 10);
        Order savedOrder = orderRepository.save(testOrder);

        mockMvc.perform(put("/api/v1/orders/" + savedOrder.getId())
                        .param("status", "RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Order updated successfully"));
    }

    @Test
    @DisplayName("Test create order with non-existent supplier")
    void testCreateOrderWithNonExistentSupplier() throws Exception {
        OrderMaterialRequestDTO materialDTO = OrderMaterialRequestDTO.builder()
                .materialId(testMaterial.getId())
                .quantity(10)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .supplierId("non-existent-id")
                .materials(List.of(materialDTO))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test create order with non-existent material")
    void testCreateOrderWithNonExistentMaterial() throws Exception {
        OrderMaterialRequestDTO materialDTO = OrderMaterialRequestDTO.builder()
                .materialId("non-existent-id")
                .quantity(10)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .supplierId(testSupplier.getId())
                .materials(List.of(materialDTO))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }
}