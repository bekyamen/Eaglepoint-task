package com.citybus.platform.modules.task.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String type,
        String status,
        OffsetDateTime timeoutAt
) {
}
