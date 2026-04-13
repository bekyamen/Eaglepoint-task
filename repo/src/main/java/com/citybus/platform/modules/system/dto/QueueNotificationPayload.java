package com.citybus.platform.modules.system.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueNotificationPayload {
    private UUID userId;
    private String type;
    private String content;
    private OffsetDateTime eventTime;
    private String dedupKey;
}
