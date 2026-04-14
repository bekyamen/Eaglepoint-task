package com.citybus.platform.modules.workflow.dto;

import java.util.UUID;

public record WorkflowTransitionDto(
        UUID id,
        String workflowName,
        String fromState,
        String toState,
        String transitionAction,
        boolean requiresApproval,
        Integer approvalChainLevel
) {
}
