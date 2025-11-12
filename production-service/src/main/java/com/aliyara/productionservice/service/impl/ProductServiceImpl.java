package com.aliyara.productionservice.service.impl;


import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.exception.RecordNotFoundException;
import com.aliyara.productionservice.mapper.ProductMapper;
import com.aliyara.productionservice.model.Product;
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
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponseDTO update(String id, ProductRequestDTO requestDTO) {
        Product existedProduct = productRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        productMapper.updateEntityFromDTO(requestDTO, existedProduct);
        return productMapper.toResponse(productRepository.save(existedProduct));
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        if(productRepository.existsById(id)){
            throw new RecordNotFoundException("Product");
        }
        productRepository.deleteById(id);
        return new ApiResponse<>(true, "Product deleted successfully", null);
    }

    @Override
    public ProductResponseDTO findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Product"));
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toResponse).toList();
    }

    @Override
    public void decreaseStock(String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("Product"));
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
