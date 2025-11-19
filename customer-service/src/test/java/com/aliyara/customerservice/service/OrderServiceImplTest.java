package com.aliyara.customerservice.service;

import com.aliyara.customerservice.client.productionClient.ProductFeignClient;
import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.dto.request.productionClient.ProductRequestDTO;
import com.aliyara.customerservice.dto.response.OrderResponseDTO;
import com.aliyara.customerservice.dto.response.productionClient.ProductResponseDTO;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.exception.productionServiceExceptions.InsufficientStockException;
import com.aliyara.customerservice.exception.productionServiceExceptions.ProductNotFoundException;
import com.aliyara.customerservice.mapper.OrderMapper;
import com.aliyara.customerservice.model.Customer;
import com.aliyara.customerservice.model.Order;
import com.aliyara.customerservice.model.ProductOrder;
import com.aliyara.customerservice.model.enums.OrderStatus;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.repository.CustomerRepository;
import com.aliyara.customerservice.repository.OrderRepository;
import com.aliyara.customerservice.repository.ProductOrderRepository;
import com.aliyara.customerservice.service.impl.OrderServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductFeignClient productFeignClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Order order;
    private OrderRequestDTO requestDTO;
    private OrderResponseDTO responseDTO;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");

        String productId = UUID.randomUUID().toString();

        productRequestDTO = ProductRequestDTO.builder()
                .productId(productId)
                .quantity(2)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(productId)
                .name("Laptop")
                .stock(10)
                .price(1200.50)
                .build();

        requestDTO = OrderRequestDTO.builder()
                .customerId(customer.getId())
                .orderStatus(OrderStatus.PENDING)
                .products(List.of(productRequestDTO))
                .build();

        order = Order.builder()
                .id(UUID.randomUUID().toString())
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .productOrders(new ArrayList<>())
                .build();

        responseDTO = OrderResponseDTO.builder()
                .id(order.getId())
                .customerId(customer.getId())
                .orderStatus(OrderStatus.PENDING)
                .productIds(List.of(productId))
                .build();
    }

    @Test
    @DisplayName("Test create order successfully")
    void testCreateOrder() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productFeignClient.getProductById(productRequestDTO.getProductId())).thenReturn(productResponseDTO);
        when(orderMapper.toEntity(requestDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productOrderRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);
        doNothing().when(productFeignClient).updateProductStock(anyString(), any(Integer.class));

        OrderResponseDTO result = orderService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(customer.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PENDING);

        verify(customerRepository, times(1)).findById(customer.getId());
//        verify(productFeignClient, times(1)).getProductById(productRequestDTO.getProductId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Test create order throws exception when customer not found")
    void testCreateOrderCustomerNotFound() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            orderService.create(requestDTO);
        });

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Test create order throws exception when insufficient stock")
    void testCreateOrderInsufficientStock() {
        ProductResponseDTO lowStockProduct = ProductResponseDTO.builder()
                .id(productRequestDTO.getProductId())
                .name("Laptop")
                .stock(1)
                .price(1200.50)
                .build();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productFeignClient.getProductById(productRequestDTO.getProductId())).thenReturn(lowStockProduct);

        assertThrows(InsufficientStockException.class, () -> {
            orderService.create(requestDTO);
        });

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(productFeignClient, times(1)).getProductById(productRequestDTO.getProductId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Test find order by id successfully")
    void testFindById() {
        String orderId = order.getId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.findById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toResponse(order);
    }

    @Test
    @DisplayName("Test find order by id throws exception when not found")
    void testFindByIdNotFound() {
        String orderId = UUID.randomUUID().toString();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            orderService.findById(orderId);
        });

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Test find all orders successfully")
    void testFindAll() {
        List<Order> orders = List.of(order);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toResponse(order);
    }

    @Test
    @DisplayName("Test find all orders throws exception when empty")
    void testFindAllEmpty() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoRecordFoundException.class, () -> {
            orderService.findAll();
        });

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test delete order successfully")
    void testDeleteOrder() {
        String orderId = order.getId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(orderId);

        ApiResponse<Void> result = orderService.delete(orderId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("Order deleted successfully");

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    @DisplayName("Test is in stock returns true when sufficient stock")
    void testIsInStockTrue() {
        when(productFeignClient.getProductById(productRequestDTO.getProductId())).thenReturn(productResponseDTO);

        Boolean result = orderService.isInStock(productRequestDTO.getProductId(), 5);

        assertThat(result).isTrue();
        verify(productFeignClient, times(1)).getProductById(productRequestDTO.getProductId());
    }

    @Test
    @DisplayName("Test is in stock returns false when insufficient stock")
    void testIsInStockFalse() {
        when(productFeignClient.getProductById(productRequestDTO.getProductId())).thenReturn(productResponseDTO);

        Boolean result = orderService.isInStock(productRequestDTO.getProductId(), 20);

        assertThat(result).isFalse();
        verify(productFeignClient, times(1)).getProductById(productRequestDTO.getProductId());
    }

    @Test
    @DisplayName("Test find orders by customer id successfully")
    void testFindByCustomerId() {
        when(orderRepository.findByCustomerId(customer.getId())).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.findByCustomerId(customer.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(orderRepository, times(1)).findByCustomerId(customer.getId());
    }
}