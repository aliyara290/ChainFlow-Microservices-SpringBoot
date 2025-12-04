package com.aliyara.supplyservice.service.impl;


import com.aliyara.supplyservice.dto.request.OrderMaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.exception.*;
import com.aliyara.supplyservice.mapper.OrderMapper;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Order;
import com.aliyara.supplyservice.model.OrderMaterial;
import com.aliyara.supplyservice.model.Supplier;
import com.aliyara.supplyservice.model.enums.OrderStatus;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.repository.OrderRepository;
import com.aliyara.supplyservice.repository.SupplierRepository;
import com.aliyara.supplyservice.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException(requestDTO.getSupplierId()));

        Order order = Order.builder()
                .supplierId(supplier.getId())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .orderMaterials(new ArrayList<>())
                .build();

        for (OrderMaterialRequestDTO item : requestDTO.getMaterials()) {
            Material material = materialRepository.findById(item.getMaterialId())
                    .orElseThrow(() -> new MaterialNotFoundException(item.getMaterialId()));
            order.addMaterial(material, item.getQuantity());
        }
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponseDTO update(String id, OrderRequestDTO requestDTO) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        existingOrder.getOrderMaterials().clear();
        for (OrderMaterialRequestDTO item : requestDTO.getMaterials()) {
            Material material = materialRepository.findById(item.getMaterialId()).orElseThrow(() -> new MaterialNotFoundException(item.getMaterialId()));
            existingOrder.addMaterial(material, item.getQuantity());
        }
        orderMapper.updateEntityFromDto(requestDTO, existingOrder);
        Order savedOrder = orderRepository.saveAndFlush(existingOrder);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
        return new ApiResponse<>(true, "Order deleted successfully", null);
    }

    @Override
    public OrderResponseDTO findById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoOrderFoundException("No Order found!");
        }
        return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public boolean updateOrderStatus(String oderId, String status) {
        Order order = orderRepository.findById(oderId)
                .orElseThrow(() -> new OrderNotFoundException(oderId));
        if (order.getStatus() != OrderStatus.RECEIVED && OrderStatus.RECEIVED.name().equalsIgnoreCase(status)) {
            for (OrderMaterial orderMaterial : order.getOrderMaterials()) {
                Material material = orderMaterial.getMaterial();
                material.setStock(material.getStock() + orderMaterial.getQuantity());
            }
        }
        order.setStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
        return true;
    }
}