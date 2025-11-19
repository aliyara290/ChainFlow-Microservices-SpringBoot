package com.aliyara.customerservice.service.impl;

import com.aliyara.customerservice.client.productionClient.ProductFeignClient;
import com.aliyara.customerservice.dto.request.OrderRequestDTO;
import com.aliyara.customerservice.dto.request.productionClient.ProductRequestDTO;
import com.aliyara.customerservice.dto.response.OrderResponseDTO;
import com.aliyara.customerservice.dto.response.productionClient.ProductResponseDTO;
import com.aliyara.customerservice.exception.FailedToInsertDataException;
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
import com.aliyara.customerservice.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductOrderRepository productOrderRepository;
    private final OrderMapper orderMapper;
    private final ProductFeignClient productFeignClient;

    @Override
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + requestDTO.getCustomerId()));

        try {
            for (ProductRequestDTO product : requestDTO.getProducts()) {
                if (!isInStock(product.getProductId(), product.getQuantity())) {
                    throw new InsufficientStockException("Insufficient stock for product" + product.getProductId());
                }
            }

            Order order = orderMapper.toEntity(requestDTO);
            order.setCustomer(customer);
            order.setOrderStatus(OrderStatus.PENDING);
            Order savedOrder = orderRepository.save(order);

            List<ProductOrder> productOrders = new ArrayList<>();
            for (ProductRequestDTO product : requestDTO.getProducts()) {
                ProductOrder productOrder = new ProductOrder();
                productOrder.setProductId(product.getProductId());
                productOrder.setOrder(savedOrder);
                productOrder.setQuantity(product.getQuantity());
                productOrders.add(productOrder);

                try {
                    updateProductStock(product.getProductId(), product.getQuantity());
                    log.info("Successfully decreased stock for product {} by {}", product.getProductId(), product.getQuantity());
                } catch (Exception e) {
                    log.error("Failed to update stock for product {}: {}", product.getProductId(), e.getMessage());
                    throw new FailedToInsertDataException("Failed to update product stock: " + e.getMessage());
                }
            }

            productOrderRepository.saveAll(productOrders);
            savedOrder.setProductOrders(productOrders);

            log.info("Order created successfully with ID: {}", savedOrder.getId());
            return orderMapper.toResponse(savedOrder);

        } catch (ProductNotFoundException | InsufficientStockException e) {
            log.error("Order creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during order creation: {}", e.getMessage(), e);
            throw new FailedToInsertDataException("Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public OrderResponseDTO update(String id, OrderRequestDTO requestDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + id));

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + requestDTO.getCustomerId()));

        try {
            List<ProductOrder> oldProductOrders = existingOrder.getProductOrders();
            List<String> oldProductIds = oldProductOrders != null
                    ? oldProductOrders.stream().map(ProductOrder::getProductId).collect(Collectors.toList())
                    : new ArrayList<>();

            for (ProductRequestDTO product : requestDTO.getProducts()) {
                ProductResponseDTO productItem = productFeignClient.getProductById(product.getProductId());
                if (productItem == null) {
                    throw new ProductNotFoundException("Product with ID " + product.getProductId() + " not found");
                }

                if (!oldProductIds.contains(product.getProductId())) {
                    if (!isInStock(product.getProductId(), product.getQuantity())) {
                        throw new InsufficientStockException(
                                String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                        product.getProductId(), productItem.getStock(), product.getQuantity())
                        );
                    }
                }
            }

            existingOrder.setOrderStatus(requestDTO.getOrderStatus());
            existingOrder.setCustomer(customer);

            if (oldProductOrders != null && !oldProductOrders.isEmpty()) {
                productOrderRepository.deleteAll(oldProductOrders);
            }

            List<ProductOrder> newProductOrders = new ArrayList<>();
            for (ProductRequestDTO product : requestDTO.getProducts()) {
                ProductOrder productOrder = new ProductOrder();
                productOrder.setProductId(product.getProductId());
                productOrder.setOrder(existingOrder);
                newProductOrders.add(productOrder);

                if (!oldProductIds.contains(product.getProductId())) {
                    updateProductStock(product.getProductId(), product.getQuantity());
                    log.info("Decreased stock for new product: {}", product.getProductId());
                }
//                else if (requestDTO.getQuantity() > existingOrder.getQuantity()) {
//                    int stockDifference = requestDTO.getQuantity() - existingOrder.getQuantity();
//                    updateProductStock(product.getProductId(), stockDifference);
//                    log.info("Decreased stock for product {} by {}", product.getProductId(), stockDifference);
//                }
            }

            productOrderRepository.saveAll(newProductOrders);
            existingOrder.setProductOrders(newProductOrders);

            Order updatedOrder = orderRepository.save(existingOrder);
            log.info("Order updated successfully: {}", id);
            return orderMapper.toResponse(updatedOrder);

        } catch (ProductNotFoundException | InsufficientStockException e) {
            log.error("Order update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during order update: {}", e.getMessage(), e);
            throw new FailedToInsertDataException("Failed to update order: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + id));

        try {
            orderRepository.deleteById(id);
            log.info("Order deleted successfully: {}", id);
            return new ApiResponse<>(true, "Order deleted successfully", null);

        } catch (Exception e) {
            log.error("Failed to delete order: {}", e.getMessage(), e);
            throw new FailedToInsertDataException("Failed to delete order: " + e.getMessage());
        }
    }

    @Override
    public OrderResponseDTO findById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoRecordFoundException("No orders found");
        }
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> findByCustomerId(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        if (orders.isEmpty()) {
            throw new NoRecordFoundException("No orders found for customer: " + customerId);
        }
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isInStock(String productId, Integer quantity) {
        try {
            ProductResponseDTO productDTO = productFeignClient.getProductById(productId);
            if (productDTO == null) {
                throw new ProductNotFoundException("Product with ID " + productId + " not found");
            }
            return productDTO.getStock() != null && productDTO.getStock() >= quantity;
        } catch (Exception e) {
            log.error("Error checking stock for product {}: {}", productId, e.getMessage());
            return false;
        }
    }

    @Override
    public void updateProductStock(String productId, Integer quantity) {
        try {
            ProductResponseDTO product = productFeignClient.getProductById(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }

            if (product.getStock() < quantity) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                productId, product.getStock(), quantity)
                );
            }

            productFeignClient.updateProductStock(productId, quantity);
            log.info("Stock updated for product {}: decreased by {}", productId, quantity);

        } catch (ProductNotFoundException | InsufficientStockException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating product stock for {}: {}", productId, e.getMessage());
            throw new FailedToInsertDataException("Failed to update product stock: " + e.getMessage());
        }
    }

}
