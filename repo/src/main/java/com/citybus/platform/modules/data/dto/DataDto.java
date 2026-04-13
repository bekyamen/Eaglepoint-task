package com.citybus.platform.modules.data.dto;

import java.util.UUID;

public record DataDto(
        UUID id,
        String sourceName,
        String versionLabel,
        boolean active
) {
}
