package com.aliyara.productionservice.integration;

import com.aliyara.productionservice.dto.request.BOMRequestDTO;
import com.aliyara.productionservice.dto.response.BOMResponseDTO;
import com.aliyara.productionservice.payload.ApiResponse;
import com.aliyara.productionservice.service.interfaces.BOMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BOMResponseDTO>> updateBOM(@PathVariable String id, @Valid @RequestBody BOMRequestDTO requestDTO) {
        BOMResponseDTO updatedBom = bomService.update(id, requestDTO);
        ApiResponse<BOMResponseDTO> response = new ApiResponse<>(true, "BOM updated successfully", updatedBom);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BOMResponseDTO>> getBOM(@PathVariable String id) {
        BOMResponseDTO bom = bomService.findById(id);
        ApiResponse<BOMResponseDTO> response = new ApiResponse<>(true, "BOM found!", bom);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BOMResponseDTO>>> getAllBOMs() {
        List<BOMResponseDTO> boms = bomService.findAll();
        ApiResponse<List<BOMResponseDTO>> response = new ApiResponse<>(true, "BOMs list", boms);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBOM(@PathVariable String id) {
        ApiResponse<Void> response = bomService.delete(id);
        return ResponseEntity.ok(response);
    }
}