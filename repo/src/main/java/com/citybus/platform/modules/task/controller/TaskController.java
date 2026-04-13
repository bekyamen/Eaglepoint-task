package com.citybus.platform.modules.task.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.service.TaskService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskDto>>> list(
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(ApiResponse.<List<TaskDto>>builder()
                .success(true)
                .data(taskService.listTasks(status))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<TaskDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .data(taskService.getTask(id))
                .build());
    }
}
