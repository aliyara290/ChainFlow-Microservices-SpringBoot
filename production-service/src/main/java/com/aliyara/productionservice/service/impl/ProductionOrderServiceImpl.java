package com.aliyara.productionservice.service.impl;

import com.aliyara.productionservice.client.MaterialFeignClient;
import com.aliyara.productionservice.dto.request.ProductionOrderRequestDTO;
import com.aliyara.productionservice.dto.response.ProductionOrderResponseDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.mapper.ProductionOrderMapper;
import com.aliyara.productionservice.model.BOM;
import com.aliyara.productionservice.model.Product;
import com.aliyara.productionservice.model.ProductionOrder;
import com.aliyara.productionservice.model.enums.ProductionOrderStatus;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.repository.ProductionOrderRepository;
import com.aliyara.productionservice.service.interfaces.ProductionOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final MaterialFeignClient materialFeignClient;

    @Override
    public ProductionOrderResponseDTO create(ProductionOrderRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product"));

        ProductionOrder productionOrder = ProductionOrder.builder()
                .quantity(Integer.parseInt(requestDTO.getQuantity()))
                .status(requestDTO.getStatus())
                .startDate(LocalDate.parse(requestDTO.getStartDate()))
                .endDate(LocalDate.parse(requestDTO.getEndDate()))
                .product(product)
                .build();

        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.toResponse(savedOrder);
    }

    @Override
    public ProductionOrderResponseDTO update(String id, ProductionOrderRequestDTO requestDTO) {
        ProductionOrder existingOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("ProductionOrder"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RecordNotFoundException("Product"));

        existingOrder.setQuantity(Integer.parseInt(requestDTO.getQuantity()));
        existingOrder.setStatus(requestDTO.getStatus());
        existingOrder.setStartDate(LocalDate.parse(requestDTO.getStartDate()));
        existingOrder.setEndDate(LocalDate.parse(requestDTO.getEndDate()));
        existingOrder.setProduct(product);

        ProductionOrder updatedOrder = productionOrderRepository.save(existingOrder);
        return productionOrderMapper.toResponse(updatedOrder);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if (!productionOrderRepository.existsById(id)) {
            throw new RecordNotFoundException("ProductionOrder");
        }
        productionOrderRepository.deleteById(id);
        return new ApiResponse<>(true, "Production order deleted successfully", null);
    }

    @Override
    public ProductionOrderResponseDTO findById(String id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("ProductionOrder"));
        return productionOrderMapper.toResponse(order);
    }

    @Override
    public List<ProductionOrderResponseDTO> findAll() {
        List<ProductionOrder> orders = productionOrderRepository.findAll();
        return orders.stream()
                .map(productionOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void onProduction(String productionOrderId) {
        ProductionOrder productionOrder = productionOrderRepository.findById(productionOrderId)
                .orElseThrow(() -> new RecordNotFoundException("Prod"));

        Product product = productionOrder.getProduct();

        List<BOM> boms = product.getBoms();

        for (BOM bom : boms) {
            MaterialDTO materialDTO = materialFeignClient.getMaterialById(bom.getMaterialId());
            int decreasedQnt = bom.getQuantity() * productionOrder.getQuantity();

            materialFeignClient.decreaseStock(materialDTO.getId(), decreasedQnt);
        }

        productionOrder.setStatus(ProductionOrderStatus.IN_PRODUCTION);

    }
}