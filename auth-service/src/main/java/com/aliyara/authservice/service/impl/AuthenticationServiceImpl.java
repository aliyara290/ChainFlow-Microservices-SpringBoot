package com.aliyara.authservice.service.impl;

import com.aliyara.authservice.dto.request.LoginRequestDTO;
import com.aliyara.authservice.dto.response.LoginResponseDTO;
import com.aliyara.authservice.exception.UnauthorizedException;
import com.aliyara.authservice.model.Role;
import com.aliyara.authservice.repository.UserRepository;
import com.aliyara.authservice.service.interfaces.AuthenticationService;
import com.aliyara.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            // Get user roles
            var user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            log.info("Login successful for user: {}", loginRequest.getUsername());

            return LoginResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .username(loginRequest.getUsername())
                    .roles(roles)
                    .build();

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}

