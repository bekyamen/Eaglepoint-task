package com.citybus.platform.modules.config.dto;

public record ConfigDto(
        String key,
        String value,
        String group
) {
}
