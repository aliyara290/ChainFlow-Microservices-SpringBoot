package com.aliyara.productionservice.service;

import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.mapper.ProductMapper;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.service.impl.ProductServiceImpl;
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
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName("Laptop");
        product.setProductionTime(120);
        product.setPrice(1200.50);
        product.setStock(50);

        requestDTO = ProductRequestDTO.builder()
                .name("Laptop")
                .productionTime(120)
                .price(1200.50)
                .stock(50)
                .build();

        responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName("Laptop");
        responseDTO.setProductionTime(120);
        responseDTO.setPrice(1200.50);
        responseDTO.setStock(50);
    }

    @Test
    @DisplayName("Test create product successfully")
    void testCreateProduct() {
        when(productMapper.toEntity(requestDTO)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualTo(1200.50);
        assertThat(result.getStock()).isEqualTo(50);

        verify(productMapper, times(1)).toEntity(requestDTO);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toResponse(product);
    }

    @Test
    @DisplayName("Test update product successfully")
    void testUpdateProduct() {
        String productId = product.getId();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntityFromDTO(requestDTO, product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.update(productId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).updateEntityFromDTO(requestDTO, product);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test update product throws exception when not found")
    void testUpdateProductNotFound() {
        String productId = UUID.randomUUID().toString();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productService.update(productId, requestDTO);
        });

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Test find product by id successfully")
    void testFindById() {
        String productId = product.getId();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.findById(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Laptop");

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).toResponse(product);
    }

    @Test
    @DisplayName("Test find product by id throws exception when not found")
    void testFindByIdNotFound() {
        String productId = UUID.randomUUID().toString();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productService.findById(productId);
        });

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Test find all products successfully")
    void testFindAll() {
        List<Product> products = List.of(product);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(responseDTO);

        List<ProductResponseDTO> result = productService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toResponse(product);
    }

    @Test
    @DisplayName("Test find all products returns empty list")
    void testFindAllEmpty() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        List<ProductResponseDTO> result = productService.findAll();

        assertThat(result).isEmpty();
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test decrease stock successfully")
    void testDecreaseStock() {
        String productId = product.getId();
        Integer quantity = 10;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.decreaseStock(productId, quantity);

        assertThat(product.getStock()).isEqualTo(40);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Test decrease stock throws exception when product not found")
    void testDecreaseStockNotFound() {
        String productId = UUID.randomUUID().toString();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productService.decreaseStock(productId, 10);
        });

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
}