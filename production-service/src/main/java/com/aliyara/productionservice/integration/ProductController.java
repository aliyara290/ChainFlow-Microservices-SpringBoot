package com.aliyara.productionservice.integration;


import com.aliyara.productionservice.dto.request.ProductRequestDTO;
import com.aliyara.productionservice.dto.response.ProductResponseDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.service.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO savedProduct = productService.create(requestDTO);
        ApiResponse<ProductResponseDTO> response = new ApiResponse<>(true, "Product created successfully", savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO savedProduct = productService.update(id, requestDTO);
        ApiResponse<ProductResponseDTO> response = new ApiResponse<>(true, "Product updated successfully", savedProduct);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id) {
        ProductResponseDTO product = productService.findById(id);
//        ApiResponse<ProductResponseDTO> response = new ApiResponse<>(true, "Product founded!", product);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productService.findAll();
        ApiResponse<List<ProductResponseDTO>> response = new ApiResponse<>(true, "Products lists!", products);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        ApiResponse<Void> deletedProduct = productService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable String id, @RequestParam Integer quantity) {
        productService.decreaseStock(id, quantity);
        ApiResponse<Void> response = new ApiResponse<>(true, "Stock updated successfully!", null);
        return ResponseEntity.ok().body(response);
    }
}
