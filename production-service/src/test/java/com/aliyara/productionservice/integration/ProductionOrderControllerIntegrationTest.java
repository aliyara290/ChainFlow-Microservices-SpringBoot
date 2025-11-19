package com.aliyara.productionservice.integration;

import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.model.ProductionOrder;
import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.repository.ProductionOrderRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductionOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private ProductionOrder testOrder;

    @BeforeEach
    void setUp() {
        productionOrderRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Laptop");
        testProduct.setProductionTime(120);
        testProduct.setPrice(1200.50);
        testProduct.setStock(50);
        testProduct = productRepository.save(testProduct);

        testOrder = ProductionOrder.builder()
                .quantity(10)
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .product(testProduct)
                .build();
    }

    @AfterEach
    void tearDown() {
        productionOrderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create production order endpoint")
    void testCreateProductionOrder() throws Exception {
        ProductionOrderRequestDTO requestDTO = ProductionOrderRequestDTO.builder()
                .quantity("10")
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(7).toString())
                .productId(testProduct.getId())
                .build();

        mockMvc.perform(post("/api/v1/production-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Production order created successfully"))
                .andExpect(jsonPath("$.data.quantity").value("10"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.productId").value(testProduct.getId()));
    }

    @Test
    @DisplayName("Test get all production orders endpoint")
    void testGetAllProductionOrders() throws Exception {
        productionOrderRepository.save(testOrder);

        mockMvc.perform(get("/api/v1/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Production orders list"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Test get production order by id endpoint")
    void testGetProductionOrderById() throws Exception {
        ProductionOrder savedOrder = productionOrderRepository.save(testOrder);

        mockMvc.perform(get("/api/v1/production-orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Production order found!"))
                .andExpect(jsonPath("$.data.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.data.quantity").value("10"));
    }

    @Test
    @DisplayName("Test update production order endpoint")
    void testUpdateProductionOrder() throws Exception {
        ProductionOrder savedOrder = productionOrderRepository.save(testOrder);

        ProductionOrderRequestDTO updateDTO = ProductionOrderRequestDTO.builder()
                .quantity("20")
                .status(ProductionOrderStatus.IN_PRODUCTION)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(10).toString())
                .productId(testProduct.getId())
                .build();

        mockMvc.perform(put("/api/v1/production-orders/" + savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Production order updated successfully"))
                .andExpect(jsonPath("$.data.quantity").value("20"))
                .andExpect(jsonPath("$.data.status").value("IN_PRODUCTION"));
    }

    @Test
    @DisplayName("Test delete production order endpoint")
    void testDeleteProductionOrder() throws Exception {
        ProductionOrder savedOrder = productionOrderRepository.save(testOrder);

        mockMvc.perform(delete("/api/v1/production-orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Production order deleted successfully"));
    }

    @Test
    @DisplayName("Test create production order with non-existent product")
    void testCreateProductionOrderWithNonExistentProduct() throws Exception {
        ProductionOrderRequestDTO requestDTO = ProductionOrderRequestDTO.builder()
                .quantity("10")
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(7).toString())
                .productId("non-existent-id")
                .build();

        mockMvc.perform(post("/api/v1/production-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test get production order by id not found")
    void testGetProductionOrderByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/production-orders/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test create production order with validation error")
    void testCreateProductionOrderValidationError() throws Exception {
        ProductionOrderRequestDTO requestDTO = ProductionOrderRequestDTO.builder()
                .quantity("")
                .status(null)
                .startDate("")
                .endDate("")
                .productId("")
                .build();

        mockMvc.perform(post("/api/v1/production-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}