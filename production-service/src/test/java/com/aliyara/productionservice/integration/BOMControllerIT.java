package com.aliyara.productionservice.integration;

import com.aliyara.productionservice.client.MaterialFeignClient;
import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.request.MaterialsDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.repository.BOMRepository;
import com.aliyara.productionservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BOMControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BOMRepository bomRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private MaterialFeignClient materialFeignClient;

    private Product testProduct;
    private BOM testBOM;
    private MaterialDTO mockMaterialDTO;

    @BeforeEach
    void setUp() {
        bomRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Laptop");
        testProduct.setProductionTime(120);
        testProduct.setPrice(1200.50);
        testProduct.setStock(50);
        testProduct = productRepository.save(testProduct);

        String materialId = UUID.randomUUID().toString();

        mockMaterialDTO = MaterialDTO.builder()
                .id(materialId)
                .name("RAM")
                .stock(100)
                .stockMin(20)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        testBOM = BOM.builder()
                .materialId(materialId)
                .quantity(10)
                .product(testProduct)
                .build();
    }

    @AfterEach
    void tearDown() {
        bomRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create BOM endpoint successfully")
    void testCreateBOM() throws Exception {
        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(mockMaterialDTO.getId());
        materialsDTO.setQuantity(10);

        BOMRequestDTO requestDTO = new BOMRequestDTO();
        requestDTO.setProductId(testProduct.getId());
        requestDTO.setMaterials(List.of(materialsDTO));

        when(materialFeignClient.getMaterialById(mockMaterialDTO.getId()))
                .thenReturn(mockMaterialDTO);

        mockMvc.perform(post("/api/v1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOM created successfully"));
    }

    @Test
    @DisplayName("Test create BOM with non-existent product")
    void testCreateBOMWithNonExistentProduct() throws Exception {
        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(mockMaterialDTO.getId());
        materialsDTO.setQuantity(10);

        BOMRequestDTO requestDTO = new BOMRequestDTO();
        requestDTO.setProductId("non-existent-id");
        requestDTO.setMaterials(List.of(materialsDTO));

        mockMvc.perform(post("/api/v1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test create BOM with non-existent material")
    void testCreateBOMWithNonExistentMaterial() throws Exception {
        String nonExistentMaterialId = UUID.randomUUID().toString();

        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(nonExistentMaterialId);
        materialsDTO.setQuantity(10);

        BOMRequestDTO requestDTO = new BOMRequestDTO();
        requestDTO.setProductId(testProduct.getId());
        requestDTO.setMaterials(List.of(materialsDTO));

        when(materialFeignClient.getMaterialById(nonExistentMaterialId))
                .thenThrow(FeignException.NotFound.class);

        mockMvc.perform(post("/api/v1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test create BOM with insufficient stock")
    void testCreateBOMWithInsufficientStock() throws Exception {
        MaterialDTO lowStockMaterial = MaterialDTO.builder()
                .id(mockMaterialDTO.getId())
                .name("RAM")
                .stock(5)
                .stockMin(20)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(mockMaterialDTO.getId());
        materialsDTO.setQuantity(10);

        BOMRequestDTO requestDTO = new BOMRequestDTO();
        requestDTO.setProductId(testProduct.getId());
        requestDTO.setMaterials(List.of(materialsDTO));

        when(materialFeignClient.getMaterialById(mockMaterialDTO.getId()))
                .thenReturn(lowStockMaterial);

        mockMvc.perform(post("/api/v1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test create BOM with multiple materials")
    void testCreateBOMWithMultipleMaterials() throws Exception {
        String material2Id = UUID.randomUUID().toString();

        MaterialDTO materialDTO2 = MaterialDTO.builder()
                .id(material2Id)
                .name("SSD")
                .stock(50)
                .stockMin(10)
                .unit("pieces")
                .supplierId(UUID.randomUUID().toString())
                .build();

        MaterialsDTO materialsDTO1 = new MaterialsDTO();
        materialsDTO1.setMaterialId(mockMaterialDTO.getId());
        materialsDTO1.setQuantity(10);

        MaterialsDTO materialsDTO2 = new MaterialsDTO();
        materialsDTO2.setMaterialId(material2Id);
        materialsDTO2.setQuantity(5);

        BOMRequestDTO requestDTO = new BOMRequestDTO();
        requestDTO.setProductId(testProduct.getId());
        requestDTO.setMaterials(List.of(materialsDTO1, materialsDTO2));

        when(materialFeignClient.getMaterialById(mockMaterialDTO.getId()))
                .thenReturn(mockMaterialDTO);
        when(materialFeignClient.getMaterialById(material2Id))
                .thenReturn(materialDTO2);

        mockMvc.perform(post("/api/v1/bom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOM created successfully"));
    }

    @Test
    @DisplayName("Test get all BOMs endpoint")
    void testGetAllBOMs() throws Exception {
        bomRepository.save(testBOM);

        mockMvc.perform(get("/api/v1/bom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOMs list"))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("Test get BOM by id endpoint")
    void testGetBOMById() throws Exception {
        BOM savedBOM = bomRepository.save(testBOM);

        mockMvc.perform(get("/api/v1/bom/" + savedBOM.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOM found!"));
    }

    @Test
    @DisplayName("Test update BOM endpoint")
    void testUpdateBOM() throws Exception {
        BOM savedBOM = bomRepository.save(testBOM);

        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(mockMaterialDTO.getId());
        materialsDTO.setQuantity(20);

        BOMRequestDTO updateDTO = new BOMRequestDTO();
        updateDTO.setProductId(testProduct.getId());
        updateDTO.setMaterials(List.of(materialsDTO));

        mockMvc.perform(put("/api/v1/bom/" + savedBOM.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOM updated successfully"));
    }

    @Test
    @DisplayName("Test delete BOM endpoint")
    void testDeleteBOM() throws Exception {
        BOM savedBOM = bomRepository.save(testBOM);

        mockMvc.perform(delete("/api/v1/bom/" + savedBOM.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("BOM deleted successfully"));
    }

    @Test
    @DisplayName("Test get BOM by id not found")
    void testGetBOMByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/bom/ad1da7fb-5ed8-48e3-8efe-c38ba426efg5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test update BOM not found")
    void testUpdateBOMNotFound() throws Exception {
        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setMaterialId(mockMaterialDTO.getId());
        materialsDTO.setQuantity(20);

        BOMRequestDTO updateDTO = new BOMRequestDTO();
        updateDTO.setProductId(testProduct.getId());
        updateDTO.setMaterials(List.of(materialsDTO));

        mockMvc.perform(put("/api/v1/bom/ad1da7fb-5ed8-48e3-8efe-c38ba426efg5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test delete BOM not found")
    void testDeleteBOMNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/bom/ad1da7fb-5ed8-48e3-8efe-c38ba426efg5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }
}