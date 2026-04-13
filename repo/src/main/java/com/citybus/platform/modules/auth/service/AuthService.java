package com.citybus.platform.modules.auth.service;

import com.citybus.platform.modules.auth.dto.AuthResponse;
import com.citybus.platform.modules.auth.dto.LoginRequest;
import com.citybus.platform.modules.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout();

    AuthResponse.UserPayload getCurrentUser();
}
