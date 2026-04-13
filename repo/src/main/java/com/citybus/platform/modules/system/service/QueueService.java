package com.citybus.platform.modules.system.service;

import com.citybus.platform.modules.system.entity.SystemEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface QueueService {

    SystemEntity enqueueMessage(String type, String payload, OffsetDateTime nextRetryAt, String idempotencyKey);

    List<SystemEntity> fetchAndLockPendingMessages(int batchSize);

    void markDone(UUID messageId);

    void markFailedWithRetry(UUID messageId, String reason);

    void recoverExpiredLocks();
}
