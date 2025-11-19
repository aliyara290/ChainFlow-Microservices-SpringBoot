package com.aliyara.supplyservice.integration;

import com.aliyara.supplyservice.dto.request.SupplierRequestDTO;
import com.aliyara.supplyservice.dto.response.SupplierResponseDTO;
import com.aliyara.supplyservice.model.Supplier;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();

        testSupplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .phone("0643569435")
                .email("ali@gmail.com")
                .rating(5.7)
                .leadTime(10)
                .build();
    }

    @AfterEach
    void tearDown() {
        supplierRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create supplier endpoint")
    void testCreateSupplier() throws Exception {
        SupplierRequestDTO requestDTO = SupplierRequestDTO.builder()
                .firstName("Ali")
                .lastName("Yara")
                .phone("0643569435")
                .email("ali@gmail.com")
                .rating(5.7)
                .leadTime(10)
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Supplier created successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Ali"))
                .andExpect(jsonPath("$.data.lastName").value("Yara"))
                .andExpect(jsonPath("$.data.email").value("ali@gmail.com"));
    }

    @Test
    @DisplayName("Test get all suppliers endpoint")
    void testGetAllSuppliers() throws Exception {
        supplierRepository.save(testSupplier);

        mockMvc.perform(get("/api/v1/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Suppliers list"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName").value("Ali"));
    }

    @Test
    @DisplayName("Test get supplier by id endpoint")
    void testGetSupplierById() throws Exception {
        Supplier savedSupplier = supplierRepository.save(testSupplier);

        mockMvc.perform(get("/api/v1/suppliers/" + savedSupplier.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Supplier found!"))
                .andExpect(jsonPath("$.data.id").value(savedSupplier.getId()))
                .andExpect(jsonPath("$.data.firstName").value("Ali"));
    }

    @Test
    @DisplayName("Test update supplier endpoint")
    void testUpdateSupplier() throws Exception {
        Supplier savedSupplier = supplierRepository.save(testSupplier);

        SupplierRequestDTO updateDTO = SupplierRequestDTO.builder()
                .firstName("Ahmed")
                .lastName("Yara")
                .phone("0643569435")
                .email("ahmed@gmail.com")
                .rating(4.8)
                .leadTime(12)
                .build();

        mockMvc.perform(put("/api/v1/suppliers/" + savedSupplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Supplier updated successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.data.email").value("ahmed@gmail.com"));
    }

    @Test
    @DisplayName("Test delete supplier endpoint")
    void testDeleteSupplier() throws Exception {
        Supplier savedSupplier = supplierRepository.save(testSupplier);

        mockMvc.perform(delete("/api/v1/suppliers/" + savedSupplier.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Supplier deleted successfully"));
    }

    @Test
    @DisplayName("Test get supplier by id not found")
    void testGetSupplierByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }
}