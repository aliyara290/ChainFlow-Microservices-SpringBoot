package com.aliyara.authservice.service.interfaces;

import com.aliyara.authservice.dto.request.LoginRequestDTO;
import com.aliyara.authservice.dto.response.LoginResponseDTO;

public interface AuthenticationService {
    LoginResponseDTO login(LoginRequestDTO loginRequest);
}

