package com.aliyara.authservice.controller;

import com.aliyara.authservice.dto.request.UserRequestDTO;
import com.aliyara.authservice.dto.response.UserResponseDTO;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.create(requestDTO);
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(
                true, "User created successfully", responseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.update(requestDTO, id);
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(
                true, "User updated successfully", responseDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        ApiResponse<Void> response = userService.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAll();
        ApiResponse<List<UserResponseDTO>> response = new ApiResponse<>(
                true, "Users fetched successfully", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable String id) {
        UserResponseDTO responseDTO = userService.findById(id);
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(
                true, "User fetched successfully", responseDTO);
        return ResponseEntity.ok(response);
    }
}