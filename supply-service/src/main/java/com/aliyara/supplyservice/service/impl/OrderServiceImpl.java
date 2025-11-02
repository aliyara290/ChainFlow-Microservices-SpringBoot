package com.aliyara.supplyservice.service.impl;


import com.aliyara.supplyservice.dto.request.OrderMaterialRequestDTO;
import com.aliyara.supplyservice.dto.request.OrderRequestDTO;
import com.aliyara.supplyservice.dto.response.OrderResponseDTO;
import com.aliyara.supplyservice.exception.MaterialNotFoundException;
import com.aliyara.supplyservice.exception.NoMaterialsFoundException;
import com.aliyara.supplyservice.exception.SupplierNotFoundException;
import com.aliyara.supplyservice.mapper.OrderMapper;
import com.aliyara.supplyservice.model.Material;
import com.aliyara.supplyservice.model.Order;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.repository.MaterialRepository;
import com.aliyara.supplyservice.repository.OrderRepository;
import com.aliyara.supplyservice.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Order order = new Order();
        order.setSupplierId(requestDTO.getSupplierId());

        for (OrderMaterialRequestDTO item : requestDTO.getMaterials()) {
            Material material = materialRepository.findById(item.getMaterialId()).orElseThrow(() -> new MaterialNotFoundException(item.getMaterialId()));
            order.addMaterial(material, item.getQuantity());
        }
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponseDTO update(String id, OrderRequestDTO requestDTO) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new SupplierNotFoundException(id));
        for (OrderMaterialRequestDTO item : requestDTO.getMaterials()) {
            Material material = materialRepository.findById(item.getMaterialId()).orElseThrow(() -> new MaterialNotFoundException(item.getMaterialId()));
            existingOrder.addMaterial(material, item.getQuantity());
        }
        orderMapper.updateEntityFromDto(requestDTO, existingOrder);
        return orderMapper.toResponse(existingOrder);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!orderRepository.existsById(id)) {
            throw new MaterialNotFoundException(id);
        }
        orderRepository.deleteById(id);
        return new ApiResponse<>(true, "Order deleted successfully", null);
    }

    @Override
    public OrderResponseDTO findById(String id) {
        Order material = orderRepository.findById(id).orElseThrow(() -> new MaterialNotFoundException(id));
        return orderMapper.toResponse(material);
    }

    @Override
    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoMaterialsFoundException();
        }
        return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }
}
