package com.citybus.platform.modules.system.service;

import com.citybus.platform.modules.system.entity.SystemEntity;
import com.citybus.platform.modules.system.repository.QueueMessageRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private static final int[] BACKOFF_SECONDS = {1, 5, 15, 60, 300};
    private static final int LOCK_TIMEOUT_SECONDS = 120;

    private final QueueMessageRepository queueMessageRepository;

    @Override
    @Transactional
    public SystemEntity enqueueMessage(String type, String payload, OffsetDateTime nextRetryAt, String idempotencyKey) {
        return queueMessageRepository.findByIdempotencyKey(idempotencyKey).orElseGet(() -> {
            OffsetDateTime now = OffsetDateTime.now();
            SystemEntity entity = SystemEntity.builder()
                    .id(UUID.randomUUID())
                    .type(type)
                    .payload(payload)
                    .status("PENDING")
                    .retryCount(0)
                    .maxRetries(5)
                    .nextRetryAt(nextRetryAt == null ? now : nextRetryAt)
                    .availableAt(now)
                    .idempotencyKey(idempotencyKey)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            return queueMessageRepository.save(entity);
        });
    }

    @Override
    @Transactional
    public List<SystemEntity> fetchAndLockPendingMessages(int batchSize) {
        OffsetDateTime now = OffsetDateTime.now();
        List<SystemEntity> due = queueMessageRepository.lockDueMessages(now, batchSize);
        for (SystemEntity message : due) {
            message.setStatus("PROCESSING");
            message.setLockedAt(now);
            message.setLockExpiresAt(now.plusSeconds(LOCK_TIMEOUT_SECONDS));
            message.setUpdatedAt(now);
        }
        return queueMessageRepository.saveAll(due);
    }

    @Override
    @Transactional
    public void markDone(UUID messageId) {
        queueMessageRepository.findById(messageId).ifPresent(message -> {
            message.setStatus("DONE");
            message.setLockedAt(null);
            message.setLockExpiresAt(null);
            message.setUpdatedAt(OffsetDateTime.now());
            queueMessageRepository.save(message);
        });
    }

    @Override
    @Transactional
    public void markFailedWithRetry(UUID messageId, String reason) {
        queueMessageRepository.findById(messageId).ifPresent(message -> {
            int newRetryCount = message.getRetryCount() + 1;
            message.setRetryCount(newRetryCount);
            message.setLockedAt(null);
            message.setLockExpiresAt(null);
            message.setUpdatedAt(OffsetDateTime.now());

            if (newRetryCount >= message.getMaxRetries()) {
                message.setStatus("FAILED");
                message.setNextRetryAt(null);
            } else {
                message.setStatus("PENDING");
                int backoffIndex = Math.min(newRetryCount - 1, BACKOFF_SECONDS.length - 1);
                message.setNextRetryAt(OffsetDateTime.now().plusSeconds(BACKOFF_SECONDS[backoffIndex]));
            }
            queueMessageRepository.save(message);
        });
    }

    @Override
    @Transactional
    public void recoverExpiredLocks() {
        OffsetDateTime now = OffsetDateTime.now();
        List<SystemEntity> expired = queueMessageRepository.findExpiredLocks(now);
        for (SystemEntity message : expired) {
            message.setStatus("PENDING");
            message.setLockedAt(null);
            message.setLockExpiresAt(null);
            message.setUpdatedAt(now);
        }
        queueMessageRepository.saveAll(expired);
    }
}
