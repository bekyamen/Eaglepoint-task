package com.citybus.platform.modules.route.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RouteDto(
        UUID id,
        String name,
        BigDecimal frequencyScore
) {
}
