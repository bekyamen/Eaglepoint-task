package com.citybus.platform.modules.data.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.data.dto.DataDto;
import com.citybus.platform.modules.data.dto.IngestionDto;
import com.citybus.platform.modules.data.service.DataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/versions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DataDto>>> listVersions() {
        return ResponseEntity.ok(ApiResponse.<List<DataDto>>builder()
                .success(true)
                .data(dataService.listVersions())
                .build());
    }

    @GetMapping("/ingestions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<IngestionDto>>> listIngestions() {
        return ResponseEntity.ok(ApiResponse.<List<IngestionDto>>builder()
                .success(true)
                .data(dataService.listIngestions())
                .build());
    }
}
