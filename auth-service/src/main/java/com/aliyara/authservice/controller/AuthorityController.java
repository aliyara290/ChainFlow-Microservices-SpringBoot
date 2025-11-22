package com.aliyara.authservice.controller;

import com.aliyara.authservice.dto.request.AuthorityRequestDTO;
import com.aliyara.authservice.dto.response.AuthorityResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.AuthorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorityResponseDTO>> createAuthority(
            @Valid @RequestBody AuthorityRequestDTO requestDTO) {
        AuthorityResponseDTO responseDTO = authorityService.create(requestDTO);

        ApiResponse<AuthorityResponseDTO> response = new ApiResponse<>(true, "Authority created successfully", responseDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorityResponseDTO>> updateAuthority(
            @PathVariable String id,
            @Valid @RequestBody AuthorityRequestDTO requestDTO) {
        AuthorityResponseDTO responseDTO = authorityService.update(requestDTO, id);

        ApiResponse<AuthorityResponseDTO> response = new ApiResponse<>(true, "Authority updated successfully", responseDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuthority(@PathVariable String id) {
        ApiResponse<Void> response = authorityService.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorityResponseDTO>>> getAllAuthorities() {
        List<AuthorityResponseDTO> authorities = authorityService.getAll();

        ApiResponse<List<AuthorityResponseDTO>> response = new ApiResponse<>(true, "Authorities fetched successfully", authorities);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorityResponseDTO>> getAuthorityById(@PathVariable String id) {
        AuthorityResponseDTO responseDTO = authorityService.findById(id);

        ApiResponse<AuthorityResponseDTO> response = new ApiResponse<>(true, "Authority fetched successfully", responseDTO);

        return ResponseEntity.ok(response);
    }
}