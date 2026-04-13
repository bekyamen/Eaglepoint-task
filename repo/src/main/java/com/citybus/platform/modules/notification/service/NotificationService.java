package com.citybus.platform.modules.notification.service;

import com.citybus.platform.modules.notification.entity.NotificationEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationEntity> listForUser(UUID userId);

    NotificationEntity createNotification(UUID userId, String type, String content, OffsetDateTime scheduledTime, String dedupKey);

    OffsetDateTime scheduleNotification(String type, OffsetDateTime eventTime);

    OffsetDateTime applyDnd(UUID userId, OffsetDateTime candidateTime);

    NotificationEntity markAsRead(UUID notificationId, UUID userId);

    void markAllAsRead(UUID userId);

    List<NotificationEntity> triggerPendingNotifications(OffsetDateTime now);
}
