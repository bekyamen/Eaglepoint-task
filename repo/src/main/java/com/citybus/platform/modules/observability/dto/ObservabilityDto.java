package com.citybus.platform.modules.observability.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ObservabilityDto(
        UUID id,
        String moduleName,
        String action,
        String entityType,
        UUID entityId,
        OffsetDateTime createdAt
) {
}
