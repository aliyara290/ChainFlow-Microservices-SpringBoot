package com.aliyara.supplyservice.controller;

import com.aliyara.supplyservice.dto.request.MaterialRequestDTO;
import com.aliyara.supplyservice.dto.response.MaterialResponseDTO;
import com.aliyara.supplyservice.payload.ApiResponse;
import com.aliyara.supplyservice.service.interfaces.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/materials")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> create(@Valid @RequestBody MaterialRequestDTO requestDTO) {
        MaterialResponseDTO material = materialService.create(requestDTO);
        ApiResponse<MaterialResponseDTO> response =
                new ApiResponse<>(true, "Material created successfully", material);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> update(@PathVariable String id,
                                                                   @Valid @RequestBody MaterialRequestDTO requestDTO) {
        MaterialResponseDTO updatedMaterial = materialService.update(id, requestDTO);
        ApiResponse<MaterialResponseDTO> response =
                new ApiResponse<>(true, "Material updated successfully", updatedMaterial);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = materialService.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> getMaterial(@PathVariable String id) {
        MaterialResponseDTO material = materialService.findById(id);
        ApiResponse<MaterialResponseDTO> response =
                new ApiResponse<>(true, "Material found!", material);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> getAllMaterials() {
        List<MaterialResponseDTO> materials = materialService.findAll();
        ApiResponse<List<MaterialResponseDTO>> response =
                new ApiResponse<>(true, "Materials list", materials);
        return ResponseEntity.ok(response);
    }
}
