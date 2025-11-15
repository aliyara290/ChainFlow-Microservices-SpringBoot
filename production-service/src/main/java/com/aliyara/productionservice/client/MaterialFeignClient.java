package com.aliyara.productionservice.client;

import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "supply-service")
public interface MaterialFeignClient {

    @GetMapping("/api/v1/materials/{id}")
    MaterialDTO getMaterialById(@PathVariable String id);
}