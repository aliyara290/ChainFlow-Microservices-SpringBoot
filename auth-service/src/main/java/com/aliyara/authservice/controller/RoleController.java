package com.aliyara.authservice.controller;

import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDTO>> createRole(
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO responseDTO = roleService.create(requestDTO);
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                true, "Role created successfully", responseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO responseDTO = roleService.update(requestDTO, id);
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                true, "Role updated successfully", responseDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String id) {
        ApiResponse<Void> response = roleService.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAllRoles() {
        List<RoleResponseDTO> roles = roleService.getAll();
        ApiResponse<List<RoleResponseDTO>> response = new ApiResponse<>(
                true, "Roles fetched successfully", roles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getRoleById(@PathVariable String id) {
        RoleResponseDTO responseDTO = roleService.findById(id);
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                true, "Role fetched successfully", responseDTO);
        return ResponseEntity.ok(response);
    }
}