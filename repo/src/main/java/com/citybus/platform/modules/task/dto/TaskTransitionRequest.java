package com.citybus.platform.modules.task.dto;

public record TaskTransitionRequest(
        String action,
        String toState,
        String notes
) {
}
