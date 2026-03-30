package com.shortstay.api.service;

import com.shortstay.api.dto.request.LoginRequest;
import com.shortstay.api.dto.request.RegisterRequest;
import com.shortstay.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}