package com.citybus.platform.modules.system.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.system.repository.QueueMessageRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/system")
@RequiredArgsConstructor
public class SystemController {

    private final QueueMessageRepository queueMessageRepository;

    @GetMapping("/queue-health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> queueHealth() {
        Map<String, Long> metrics = Map.of(
                "pending", queueMessageRepository.countByStatus("PENDING"),
                "processing", queueMessageRepository.countByStatus("PROCESSING"),
                "failed", queueMessageRepository.countByStatus("FAILED"),
                "done", queueMessageRepository.countByStatus("DONE")
        );
        return ResponseEntity.ok(ApiResponse.<Map<String, Long>>builder()
                .success(true)
                .data(metrics)
                .build());
    }
}
