package com.aliyara.authservice.controller;

import com.aliyara.authservice.dto.request.LoginRequestDTO;
import com.aliyara.authservice.dto.response.LoginResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Login successful",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Logout successful",
                null
        );
        return ResponseEntity.ok(response);
    }
}