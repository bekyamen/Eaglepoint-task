package com.citybus.platform.modules.stop.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.stop.dto.StopDto;
import com.citybus.platform.modules.stop.service.StopService;
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
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
public class StopController {

    private final StopService stopService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<StopDto>>> list() {
        return ResponseEntity.ok(ApiResponse.<List<StopDto>>builder()
                .success(true)
                .data(stopService.listStops())
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<StopDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<StopDto>builder()
                .success(true)
                .data(stopService.getStop(id))
                .build());
    }
}
