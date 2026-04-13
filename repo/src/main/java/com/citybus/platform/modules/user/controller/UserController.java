package com.citybus.platform.modules.user.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.auth.dto.AuthResponse;
import com.citybus.platform.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse.UserPayload>> me() {
        return ResponseEntity.ok(ApiResponse.<AuthResponse.UserPayload>builder()
                .success(true)
                .data(authService.getCurrentUser())
                .build());
    }
}
