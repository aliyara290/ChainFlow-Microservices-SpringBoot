package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.RoleRequestDTO;
import com.aliyara.authservice.dto.request.UserRequestDTO;
import com.aliyara.authservice.dto.response.UserResponseDTO;
import com.aliyara.authservice.exception.DuplicateRecordException;
import com.aliyara.authservice.exception.ResourceNotFoundException;
import com.aliyara.authservice.mapper.UserMapper;
import com.aliyara.authservice.model.AppUser;
import com.aliyara.authservice.model.Role;
import com.aliyara.authservice.payload.ApiResponse;
import com.aliyara.authservice.repository.RoleRepository;
import com.aliyara.authservice.repository.UserRepository;
import com.aliyara.authservice.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        log.info("Creating new user with username: {}", requestDTO.getUsername());

        if (userRepository.existsAppUsersByUsername(requestDTO.getUsername())) {
            throw new DuplicateRecordException("User with username '" + requestDTO.getUsername() + "' already exists");
        }

        if (userRepository.existsAppUsersByEmail(requestDTO.getEmail())) {
            throw new DuplicateRecordException("User with email '" + requestDTO.getEmail() + "' already exists");
        }

        AppUser user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if (requestDTO.getRoles() != null && !requestDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (RoleRequestDTO roleDTO : requestDTO.getRoles()) {
                Role role = roleRepository.findByName(roleDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found with name: " + roleDTO.getName()));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        AppUser savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDTO update(UserRequestDTO requestDTO, String id) {
        log.info("Updating user with id: {}", id);

        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " not found!"));

        if (!existingUser.getUsername().equals(requestDTO.getUsername())
                && userRepository.existsAppUsersByUsername(requestDTO.getUsername())) {
            throw new DuplicateRecordException("User with username '" + requestDTO.getUsername() + "' already exists");
        }

        if (!existingUser.getEmail().equals(requestDTO.getEmail())
                && userRepository.existsAppUsersByEmail(requestDTO.getEmail())) {
            throw new DuplicateRecordException("User with email '" + requestDTO.getEmail() + "' already exists");
        }

        existingUser.setFirstName(requestDTO.getFirstName());
        existingUser.setLastName(requestDTO.getLastName());
        existingUser.setUsername(requestDTO.getUsername());
        existingUser.setEmail(requestDTO.getEmail());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        if (requestDTO.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (RoleRequestDTO roleDTO : requestDTO.getRoles()) {
                Role role = roleRepository.findByName(roleDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found with name: " + roleDTO.getName()));
                roles.add(role);
            }
            existingUser.setRoles(roles);
        }

        AppUser savedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", id);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public ApiResponse<Void> delete(String id) {
        log.info("Deleting user with id: {}", id);

        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " not found!"));

        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
        return new ApiResponse<>(true, "User deleted successfully", null);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        log.info("Fetching all users");
        List<AppUser> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(String id) {
        log.info("Fetching user with id: {}", id);
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " not found!"));
        return userMapper.toResponse(user);
    }
}