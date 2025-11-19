package com.aliyara.productionservice.service;

import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.mapper.ProductionOrderMapper;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.model.ProductionOrder;
import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.repository.ProductionOrderRepository;
import com.aliyara.productionservice.service.impl.ProductionOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderServiceImplTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductionOrderMapper productionOrderMapper;

    @InjectMocks
    private ProductionOrderServiceImpl productionOrderService;

    private Product product;
    private ProductionOrder productionOrder;
    private ProductionOrderRequestDTO requestDTO;
    private ProductionOrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName("Laptop");
        product.setProductionTime(120);
        product.setPrice(1200.50);
        product.setStock(50);

        productionOrder = ProductionOrder.builder()
                .id(UUID.randomUUID().toString())
                .quantity(10)
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .product(product)
                .build();

        requestDTO = ProductionOrderRequestDTO.builder()
                .quantity("10")
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(7).toString())
                .productId(product.getId())
                .build();

        responseDTO = ProductionOrderResponseDTO.builder()
                .id(productionOrder.getId())
                .quantity("10")
                .status(ProductionOrderStatus.PENDING)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(7).toString())
                .productId(product.getId())
                .build();
    }

    @Test
    @DisplayName("Test create production order successfully")
    void testCreateProductionOrder() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toResponse(any(ProductionOrder.class))).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo("10");
        assertThat(result.getStatus()).isEqualTo(ProductionOrderStatus.PENDING);
        assertThat(result.getProductId()).isEqualTo(product.getId());

        verify(productRepository, times(1)).findById(product.getId());
        verify(productionOrderRepository, times(1)).save(any(ProductionOrder.class));
        verify(productionOrderMapper, times(1)).toResponse(any(ProductionOrder.class));
    }

    @Test
    @DisplayName("Test create production order throws exception when product not found")
    void testCreateProductionOrderProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productionOrderService.create(requestDTO);
        });

        verify(productRepository, times(1)).findById(product.getId());
        verify(productionOrderRepository, never()).save(any(ProductionOrder.class));
    }

    @Test
    @DisplayName("Test update production order successfully")
    void testUpdateProductionOrder() {
        String orderId = productionOrder.getId();

        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toResponse(any(ProductionOrder.class))).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.update(orderId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);

        verify(productionOrderRepository, times(1)).findById(orderId);
        verify(productRepository, times(1)).findById(product.getId());
        verify(productionOrderRepository, times(1)).save(any(ProductionOrder.class));
    }

    @Test
    @DisplayName("Test update production order throws exception when order not found")
    void testUpdateProductionOrderNotFound() {
        String orderId = UUID.randomUUID().toString();

        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productionOrderService.update(orderId, requestDTO);
        });

        verify(productionOrderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Test delete production order successfully")
    void testDeleteProductionOrder() {
        String orderId = productionOrder.getId();

        when(productionOrderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(productionOrderRepository).deleteById(orderId);

        ApiResponse<Void> result = productionOrderService.delete(orderId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("Production order deleted successfully");

        verify(productionOrderRepository, times(1)).existsById(orderId);
        verify(productionOrderRepository, times(1)).deleteById(orderId);
    }

    @Test
    @DisplayName("Test delete production order throws exception when not found")
    void testDeleteProductionOrderNotFound() {
        String orderId = UUID.randomUUID().toString();

        when(productionOrderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            productionOrderService.delete(orderId);
        });

        verify(productionOrderRepository, times(1)).existsById(orderId);
        verify(productionOrderRepository, never()).deleteById(orderId);
    }

    @Test
    @DisplayName("Test find production order by id successfully")
    void testFindById() {
        String orderId = productionOrder.getId();

        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));
        when(productionOrderMapper.toResponse(productionOrder)).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.findById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);

        verify(productionOrderRepository, times(1)).findById(orderId);
        verify(productionOrderMapper, times(1)).toResponse(productionOrder);
    }

    @Test
    @DisplayName("Test find production order by id throws exception when not found")
    void testFindByIdNotFound() {
        String orderId = UUID.randomUUID().toString();

        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            productionOrderService.findById(orderId);
        });

        verify(productionOrderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Test find all production orders successfully")
    void testFindAll() {
        List<ProductionOrder> orders = List.of(productionOrder);

        when(productionOrderRepository.findAll()).thenReturn(orders);
        when(productionOrderMapper.toResponse(productionOrder)).thenReturn(responseDTO);

        List<ProductionOrderResponseDTO> result = productionOrderService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(product.getId());

        verify(productionOrderRepository, times(1)).findAll();
        verify(productionOrderMapper, times(1)).toResponse(productionOrder);
    }
}