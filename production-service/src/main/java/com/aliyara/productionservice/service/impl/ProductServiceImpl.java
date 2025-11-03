package com.aliyara.productionservice.service.impl;


import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.mapper.ProductMapper;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.repository.ProductRepository;
import com.aliyara.productionservice.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO create(ProductRequestDTO t) {
        return null;
    }

    @Override
    public ProductResponseDTO update(String id, ProductRequestDTO t) {
        return null;
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        return null;
    }

    @Override
    public ProductResponseDTO findById(String id) {
        return null;
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return List.of();
    }
}
