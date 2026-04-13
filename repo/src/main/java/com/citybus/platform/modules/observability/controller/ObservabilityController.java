package com.citybus.platform.modules.observability.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.observability.dto.ObservabilityDto;
import com.citybus.platform.modules.observability.service.ObservabilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/observability")
@RequiredArgsConstructor
public class ObservabilityController {

    private final ObservabilityService observabilityService;

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ObservabilityDto>>> listAuditLogs() {
        return ResponseEntity.ok(ApiResponse.<List<ObservabilityDto>>builder()
                .success(true)
                .data(observabilityService.listAuditLogs())
                .build());
    }
}
