package com.citybus.platform.modules.config.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.config.dto.ConfigDto;
import com.citybus.platform.modules.config.service.ConfigService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ConfigDto>>> list() {
        return ResponseEntity.ok(ApiResponse.<List<ConfigDto>>builder()
                .success(true)
                .data(configService.listConfigs())
                .build());
    }

    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ConfigDto>>> listTemplates() {
        return ResponseEntity.ok(ApiResponse.<List<ConfigDto>>builder()
                .success(true)
                .data(configService.listTemplates())
                .build());
    }

    @GetMapping("/dictionaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ConfigDto>>> listDictionaries() {
        return ResponseEntity.ok(ApiResponse.<List<ConfigDto>>builder()
                .success(true)
                .data(configService.listDictionaries())
                .build());
    }
}
