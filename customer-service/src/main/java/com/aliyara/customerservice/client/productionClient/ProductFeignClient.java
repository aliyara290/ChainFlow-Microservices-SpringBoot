package com.aliyara.customerservice.client.productionClient;


import com.aliyara.customerservice.dto.response.productionClient.ProductResponseDTO;
import com.aliyara.customerservice.payload.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient (name = "production-service")
public interface ProductFeignClient {
    @GetMapping("/api/v1/products/{id}")
    ProductResponseDTO getProductById(@PathVariable String id);

    @PutMapping("/api/v1/products/{id}/stock")
    void updateProductStock(@PathVariable String id, @RequestParam Integer quantity);
}
