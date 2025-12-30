package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.LoginRequestDTO;
import com.aliyara.authservice.dto.response.LoginResponseDTO;
import com.aliyara.authservice.dto.response.RoleResponseDTO;
import com.aliyara.authservice.exception.UnauthorizedException;
import com.aliyara.authservice.mapper.RoleMapper;
import com.aliyara.authservice.model.AppUser;
import com.aliyara.authservice.repository.UserRepository;
import com.aliyara.authservice.security.CustomUserDetails;
import com.aliyara.authservice.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Attempting login for username: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser user = userDetails.getUser();

            log.info("User {} logged in successfully", user.getUsername());

            return new LoginResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRoles().stream()
                            .map(roleMapper::toResponse)
                            .collect(Collectors.toSet())
            );

        } catch (BadCredentialsException e) {
            log.error("Failed login attempt for username: {}", loginRequest.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    @Override
    public void logout() {
        log.info("User logged out");
        SecurityContextHolder.clearContext();
    }
}