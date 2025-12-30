package com.aliyara.authservice.controller;

import com.aliyara.authservice.dto.request.LoginRequestDTO;
import com.aliyara.authservice.dto.response.LoginResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authenticationService.login(loginRequest);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(
                true, "Login successful", response);
        return ResponseEntity.ok(apiResponse);
    }
}

