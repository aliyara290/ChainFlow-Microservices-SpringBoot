package com.aliyara.productionservice.controller;

import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.response.material.MaterialDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.service.interfaces.BOMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bom")
public class BOMController {

    private final BOMService bomService;


    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBOM(@Valid @RequestBody BOMRequestDTO requestDTO) {
        ApiResponse<Void> savedBom = bomService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBom);
    }
}
