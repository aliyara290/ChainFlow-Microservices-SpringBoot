package com.aliyara.productionservice.integration;

import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.repository.ProductRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Laptop");
        testProduct.setProductionTime(120);
        testProduct.setPrice(1200.50);
        testProduct.setStock(50);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create product endpoint")
    void testCreateProduct() throws Exception {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Laptop")
                .productionTime(120)
                .price(1200.50)
                .stock(50)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.data.price").value(1200.50))
                .andExpect(jsonPath("$.data.stock").value(50));
    }

    @Test
    @DisplayName("Test get all products endpoint")
    void testGetAllProducts() throws Exception {
        productRepository.save(testProduct);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Products lists!"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Laptop"));
    }

    @Test
    @DisplayName("Test get product by id endpoint")
    void testGetProductById() throws Exception {
        Product savedProduct = productRepository.save(testProduct);

        mockMvc.perform(get("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    @DisplayName("Test update product endpoint")
    void testUpdateProduct() throws Exception {
        Product savedProduct = productRepository.save(testProduct);

        ProductRequestDTO updateDTO = ProductRequestDTO.builder()
                .name("Updated Laptop")
                .productionTime(150)
                .price(1500.00)
                .stock(100)
                .build();

        mockMvc.perform(put("/api/v1/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Product updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.data.price").value(1500.00));
    }

    @Test
    @DisplayName("Test delete product endpoint")
    void testDeleteProduct() throws Exception {
        Product savedProduct = productRepository.save(testProduct);
        System.out.println("LLllllllllLllllllLlLLlllll"+savedProduct);

        mockMvc.perform(delete("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @DisplayName("Test update product stock endpoint")
    void testUpdateProductStock() throws Exception {
        Product savedProduct = productRepository.save(testProduct);

        mockMvc.perform(put("/api/v1/products/" + savedProduct.getId() + "/stock")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Stock updated successfully!"));
    }

    @Test
    @DisplayName("Test get product by id not found")
    void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test create product with validation error")
    void testCreateProductValidationError() throws Exception {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("")
                .productionTime(-10)
                .price(-100)
                .stock(-5)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}