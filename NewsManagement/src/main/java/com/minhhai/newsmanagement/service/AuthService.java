package com.minhhai.newsmanagement.service;

import com.minhhai.newsmanagement.dto.request.LoginRequest;
import com.minhhai.newsmanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
