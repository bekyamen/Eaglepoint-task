package com.citybus.platform.modules.workflow.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.workflow.dto.WorkflowDto;
import com.citybus.platform.modules.workflow.dto.WorkflowTransitionDto;
import com.citybus.platform.modules.workflow.service.WorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowDto>>> listStates() {
        return ResponseEntity.ok(ApiResponse.<List<WorkflowDto>>builder()
                .success(true)
                .data(workflowService.listStates())
                .build());
    }

    @GetMapping("/transitions")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowTransitionDto>>> listTransitions(
            @RequestParam(value = "workflowName", required = false) String workflowName,
            @RequestParam(value = "fromState", required = false) String fromState
    ) {
        return ResponseEntity.ok(ApiResponse.<List<WorkflowTransitionDto>>builder()
                .success(true)
                .data(workflowService.listTransitions(workflowName, fromState))
                .build());
    }
}
