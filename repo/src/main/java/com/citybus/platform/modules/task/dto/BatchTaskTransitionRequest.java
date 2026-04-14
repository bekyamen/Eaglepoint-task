package com.citybus.platform.modules.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record BatchTaskTransitionRequest(
        @NotEmpty List<UUID> taskIds,
        @NotNull
        @Valid TaskTransitionRequest transition
) {
}
