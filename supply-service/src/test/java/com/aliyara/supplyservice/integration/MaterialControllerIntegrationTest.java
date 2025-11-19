package com.aliyara.supplyservice.integration;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.repository.MaterialRepository;
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

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MaterialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier testSupplier;
    private Material testMaterial;

    @BeforeEach
    void setUp() {
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
    }

    @AfterEach
    void tearDown() {
        materialRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create material endpoint")
    void testCreateMaterial() throws Exception {
        MaterialRequestDTO requestDTO = MaterialRequestDTO.builder()
                .name("RAM")
                .stock(50)
                .stockMin(20)
                .unit("kg")
                .supplierId(testSupplier.getId())
                .build();

        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Material created successfully"))
                .andExpect(jsonPath("$.data.name").value("RAM"))
                .andExpect(jsonPath("$.data.stock").value(50))
                .andExpect(jsonPath("$.data.unit").value("kg"));
    }

    @Test
    @DisplayName("Test get all materials endpoint")
    void testGetAllMaterials() throws Exception {
        materialRepository.save(testMaterial);

        mockMvc.perform(get("/api/v1/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Materials list"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("RAM"));
    }

    @Test
    @DisplayName("Test get material by id endpoint")
    void testGetMaterialById() throws Exception {
        Material savedMaterial = materialRepository.save(testMaterial);

        mockMvc.perform(get("/api/v1/materials/" + savedMaterial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMaterial.getId()))
                .andExpect(jsonPath("$.name").value("RAM"))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    @DisplayName("Test update material endpoint")
    void testUpdateMaterial() throws Exception {
        Material savedMaterial = materialRepository.save(testMaterial);

        MaterialRequestDTO updateDTO = MaterialRequestDTO.builder()
                .name("Updated RAM")
                .stock(100)
                .stockMin(30)
                .unit("kg")
                .supplierId(testSupplier.getId())
                .build();

        mockMvc.perform(put("/api/v1/materials/" + savedMaterial.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Material updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated RAM"))
                .andExpect(jsonPath("$.data.stock").value(100));
    }

    @Test
    @DisplayName("Test delete material endpoint")
    void testDeleteMaterial() throws Exception {
        Material savedMaterial = materialRepository.save(testMaterial);

        mockMvc.perform(delete("/api/v1/materials/" + savedMaterial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Material deleted successfully"));
    }

    @Test
    @DisplayName("Test get material by id not found")
    void testGetMaterialByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/materials/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test create material with validation error")
    void testCreateMaterialValidationError() throws Exception {
        MaterialRequestDTO requestDTO = MaterialRequestDTO.builder()
                .name("")  // Invalid: blank name
                .stock(-10)  // Invalid: negative stock
                .stockMin(20)
                .unit("kg")
                .supplierId(testSupplier.getId())
                .build();

        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}