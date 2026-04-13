package com.citybus.platform.modules.route.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.route.dto.RouteDto;
import com.citybus.platform.modules.route.service.RouteService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<RouteDto>>> list() {
        return ResponseEntity.ok(ApiResponse.<List<RouteDto>>builder()
                .success(true)
                .data(routeService.listRoutes())
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<RouteDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<RouteDto>builder()
                .success(true)
                .data(routeService.getRoute(id))
                .build());
    }
}
