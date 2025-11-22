package com.aliyara.authservice.service.impl;


import com.aliyara.authservice.dto.request.UserRequestDTO;
import com.aliyara.authservice.dto.response.UserResponseDTO;
import com.aliyara.authservice.exception.DuplicateRecordException;
import com.aliyara.authservice.exception.ResourceNotFoundException;
import com.aliyara.authservice.mapper.UserMapper;
import com.aliyara.authservice.model.AppUser;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.repository.UserRepository;
import com.aliyara.authservice.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if(userRepository.existsAppUsersByUsername(requestDTO.getUsername()) ||
                userRepository.existsAppUsersByEmail(requestDTO.getEmail())
        ) {
            throw new DuplicateRecordException("User Already exit!");
        }

        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        AppUser user = userMapper.toEntity(requestDTO);
        AppUser savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDTO update(UserRequestDTO requestDTO, String id) {
        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + "not found!"));

        if(requestDTO.getPassword() != null) {
            requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        userMapper.updateEntityFromDTO(requestDTO, existingUser);

        AppUser savedUser = userRepository.save(existingUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + "not found!"));
        userRepository.delete(user);
        return new ApiResponse<>(true, "User Deleted successfully", null);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        List<AppUser> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + "not found!"));
        return userMapper.toResponse(user);
    }
}
