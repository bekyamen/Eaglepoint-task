package com.citybus.platform.modules.data.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IngestionDto(
        UUID id,
        String sourceName,
        String ingestStatus,
        String versionLabel,
        OffsetDateTime receivedAt
) {
}
