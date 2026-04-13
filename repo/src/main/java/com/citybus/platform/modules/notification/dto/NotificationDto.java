package com.citybus.platform.modules.notification.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        String type,
        String content,
        String status,
        OffsetDateTime scheduledTime,
        OffsetDateTime createdAt
) {
}
