package com.citybus.platform.modules.workflow.dto;

import java.util.UUID;

public record WorkflowDto(
        UUID id,
        String workflowName,
        String currentState
) {
}
