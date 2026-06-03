package com.ardms.service;

import com.ardms.dto.request.LoginRequest;
import com.ardms.dto.request.RegisterRequest;
import com.ardms.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String username);
}
