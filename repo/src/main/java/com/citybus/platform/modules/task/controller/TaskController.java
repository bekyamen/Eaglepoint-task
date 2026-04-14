package com.citybus.platform.modules.task.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.auth.security.UserPrincipal;
import com.citybus.platform.modules.task.dto.BatchTaskTransitionRequest;
import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.dto.TaskTransitionRequest;
import com.citybus.platform.modules.task.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
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

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<TaskDto>> approve(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) TaskTransitionRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId
    ) {
        return transitionWithAction(id, request, "approve", principal, traceId);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<TaskDto>> reject(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) TaskTransitionRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId
    ) {
        return transitionWithAction(id, request, "reject", principal, traceId);
    }

    @PostMapping("/{id}/branch")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<TaskDto>> branch(
            @PathVariable UUID id,
            @Valid @RequestBody TaskTransitionRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId
    ) {
        return transitionWithAction(id, request, "branch", principal, traceId);
    }

    @PostMapping("/{id}/escalate")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<TaskDto>> escalate(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) TaskTransitionRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId
    ) {
        return transitionWithAction(id, request, "escalate", principal, traceId);
    }

    @PostMapping("/batch-transition")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskDto>>> batchTransition(
            @Valid @RequestBody BatchTaskTransitionRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId
    ) {
        return ResponseEntity.ok(ApiResponse.<List<TaskDto>>builder()
                .success(true)
                .data(taskService.batchTransition(request.taskIds(), request.transition(), principal.getId(), traceId))
                .build());
    }

    private ResponseEntity<ApiResponse<TaskDto>> transitionWithAction(
            UUID taskId,
            TaskTransitionRequest request,
            String action,
            UserPrincipal principal,
            String traceId
    ) {
        TaskTransitionRequest effectiveRequest = request == null
                ? new TaskTransitionRequest(action, null, null)
                : new TaskTransitionRequest(action, request.toState(), request.notes());
        log.info("Task transition requested taskId={} action={} actor={}", taskId, action, principal.getUsername());
        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .data(taskService.transitionTask(taskId, effectiveRequest, principal.getId(), traceId))
                .build());
    }
}
