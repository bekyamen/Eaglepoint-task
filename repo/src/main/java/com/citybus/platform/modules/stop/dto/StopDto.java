package com.citybus.platform.modules.stop.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StopDto(
        UUID id,
        String name,
        BigDecimal popularityScore
) {
}
