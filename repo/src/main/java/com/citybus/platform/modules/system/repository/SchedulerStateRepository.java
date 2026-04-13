package com.citybus.platform.modules.system.repository;

import com.citybus.platform.modules.system.entity.SchedulerStateEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerStateRepository extends JpaRepository<SchedulerStateEntity, UUID> {
    Optional<SchedulerStateEntity> findBySchedulerKey(String schedulerKey);
}
