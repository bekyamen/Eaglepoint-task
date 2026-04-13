package com.citybus.platform.modules.notification.repository;

import com.citybus.platform.modules.notification.entity.NotificationPreferenceEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, UUID> {
    Optional<NotificationPreferenceEntity> findByUserId(UUID userId);
}
