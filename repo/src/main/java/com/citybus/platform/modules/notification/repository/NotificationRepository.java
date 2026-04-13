package com.citybus.platform.modules.notification.repository;

import com.citybus.platform.modules.notification.entity.NotificationEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Optional<NotificationEntity> findByDedupKey(String dedupKey);

    List<NotificationEntity> findTop100ByStatusAndScheduledTimeLessThanEqualOrderByScheduledTimeAsc(
            String status, OffsetDateTime scheduledTime
    );

    List<NotificationEntity> findTop200ByUserIdOrderByCreatedAtDesc(UUID userId);
}
