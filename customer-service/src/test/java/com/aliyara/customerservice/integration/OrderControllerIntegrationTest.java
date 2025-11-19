package com.aliyara.customerservice.integration;

import com.aliyara.customerservice.client.productionClient.ProductFeignClient;
import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.dto.request.productionClient.ProductRequestDTO;
import com.aliyara.customerservice.dto.response.productionClient.ProductResponseDTO;
import com.aliyara.customerservice.model.Adresse;
import com.aliyara.customerservice.model.Customer;
import com.aliyara.customerservice.model.Order;
import com.aliyara.customerservice.model.enums.OrderStatus;
import com.aliyara.customerservice.repository.CustomerRepository;
import com.aliyara.customerservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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
    private CustomerRepository customerRepository;

    @MockBean
    private ProductFeignClient productFeignClient;

    private Customer testCustomer;
    private Order testOrder;
    private String testProductId;
    private ProductResponseDTO mockProductResponse;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();

        Adresse adresse = new Adresse();
        adresse.setStreet("123 Main St");
        adresse.setCity("New York");
        adresse.setState("NY");
        adresse.setCountry("USA");
        adresse.setZip("10001");

        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("+1234567890");
        testCustomer.setAdresse(adresse);
        testCustomer = customerRepository.save(testCustomer);

        testProductId = UUID.randomUUID().toString();
        mockProductResponse = ProductResponseDTO.builder()
                .id(testProductId)
                .name("Laptop")
                .stock(100)
                .price(1200.50)
                .productionTime(120)
                .build();

        testOrder = Order.builder()
                .customer(testCustomer)
                .orderStatus(OrderStatus.PENDING)
                .productOrders(new ArrayList<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create order endpoint successfully")
    void testCreateOrder() throws Exception {
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .productId(testProductId)
                .quantity(2)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .customerId(testCustomer.getId())
                .orderStatus(OrderStatus.PENDING)
                .products(List.of(productRequest))
                .build();

        when(productFeignClient.getProductById(testProductId)).thenReturn(mockProductResponse);
        doNothing().when(productFeignClient).updateProductStock(testProductId, 1);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.data.orderStatus").value("PENDING"));

        verify(productFeignClient, times(1)).updateProductStock(testProductId, 2);
    }

//    @Test
//    @DisplayName("Test create order with multiple products")
//    void testCreateOrderWithMultipleProducts() throws Exception {
//        String product2Id = UUID.randomUUID().toString();
//        ProductResponseDTO product2Response = ProductResponseDTO.builder()
//                .id(product2Id)
//                .name("Mouse")
//                .stock(50)
//                .price(25.99)
//                .productionTime(30)
//                .build();
//
//        ProductRequestDTO productRequest1 = ProductRequestDTO.builder()
//                .productId(testProductId)
//                .quantity(2)
//                .build();
//
//        ProductRequestDTO productRequest2 = ProductRequestDTO.builder()
//                .productId(product2Id)
//                .quantity(3)
//                .build();
//
//        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
//                .customerId(testCustomer.getId())
//                .orderStatus(OrderStatus.PENDING)
//                .products(List.of(productRequest1, productRequest2))
//                .build();
//
//        when(productFeignClient.getProductById(testProductId)).thenReturn(mockProductResponse);
//        when(productFeignClient.getProductById(product2Id)).thenReturn(product2Response);
//        doNothing().when(productFeignClient).updateProductStock(anyString(), any(Integer.class));
//
//        mockMvc.perform(post("/api/v1/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.data.customerId").value(testCustomer.getId()));
//
//        verify(productFeignClient, times(1)).getProductById(testProductId);
//        verify(productFeignClient, times(1)).getProductById(product2Id);
//        verify(productFeignClient, times(1)).updateProductStock(testProductId, 2);
//        verify(productFeignClient, times(1)).updateProductStock(product2Id, 3);
//    }

    @Test
    @DisplayName("Test create order with non-existent customer")
    void testCreateOrderWithNonExistentCustomer() throws Exception {
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .productId(testProductId)
                .quantity(2)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .customerId("non-existent-id")
                .orderStatus(OrderStatus.PENDING)
                .products(List.of(productRequest))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));

        verify(productFeignClient, never()).getProductById(anyString());
        verify(productFeignClient, never()).updateProductStock(anyString(), any(Integer.class));
    }

    @Test
    @DisplayName("Test create order with non-existent product")
    void testCreateOrderWithNonExistentProduct() throws Exception {
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .productId(testProductId)
                .quantity(2)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .customerId(testCustomer.getId())
                .orderStatus(OrderStatus.PENDING)
                .products(List.of(productRequest))
                .build();

        when(productFeignClient.getProductById(testProductId)).thenReturn(null);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false));

        verify(productFeignClient, times(1)).getProductById(testProductId);
        verify(productFeignClient, never()).updateProductStock(anyString(), any(Integer.class));
    }

    @Test
    @DisplayName("Test create order with insufficient stock")
    void testCreateOrderWithInsufficientStock() throws Exception {
        ProductResponseDTO lowStockProduct = ProductResponseDTO.builder()
                .id(testProductId)
                .name("Laptop")
                .stock(1)
                .price(1200.50)
                .productionTime(120)
                .build();

        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .productId(testProductId)
                .quantity(10)
                .build();

        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .customerId(testCustomer.getId())
                .orderStatus(OrderStatus.PENDING)
                .products(List.of(productRequest))
                .build();

        when(productFeignClient.getProductById(testProductId)).thenReturn(lowStockProduct);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false));

//        verify(productFeignClient, times(2)).getProductById(testProductId);
        verify(productFeignClient, never()).updateProductStock(anyString(), any(Integer.class));
    }

    @Test
    @DisplayName("Test get all orders endpoint")
    void testGetAllOrders() throws Exception {
        Order savedOrder = orderRepository.save(testOrder);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].customerId").value(testCustomer.getId()));
    }

    @Test
    @DisplayName("Test get order by id endpoint")
    void testGetOrderById() throws Exception {
        Order savedOrder = orderRepository.save(testOrder);

        mockMvc.perform(get("/api/v1/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Order retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.data.customerId").value(testCustomer.getId()));
    }

    @Test
    @DisplayName("Test delete order endpoint")
    void testDeleteOrder() throws Exception {
        Order savedOrder = orderRepository.save(testOrder);

        mockMvc.perform(delete("/api/v1/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Order deleted successfully"));
    }

    @Test
    @DisplayName("Test get order by id not found")
    void testGetOrderByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test delete order not found")
    void testDeleteOrderNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test update order not found")
    void testUpdateOrderNotFound() throws Exception {
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .productId(testProductId)
                .quantity(2)
                .build();

        OrderRequestDTO updateDTO = OrderRequestDTO.builder()
                .customerId(testCustomer.getId())
                .orderStatus(OrderStatus.CONFIRMED)
                .products(List.of(productRequest))
                .build();

        mockMvc.perform(put("/api/v1/orders/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test get all orders when empty")
    void testGetAllOrdersEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }
}