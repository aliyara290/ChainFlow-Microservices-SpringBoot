package com.aliyara.productionservice.client;

import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import jakarta.ws.rs.PathParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "supply-service")
public interface MaterialFeignClient {

    @GetMapping("/api/v1/materials/{id}")
    MaterialDTO getMaterialById(@PathVariable String id);

    @PutMapping("/api/v1/materials/stock/{id}/{quantity}")
    void decreaseStock(@PathVariable String id, @PathVariable int quantity);
}