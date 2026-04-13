package com.citybus.platform.modules.observability.repository;

import com.citybus.platform.modules.observability.entity.ObservabilityEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservabilityRepository extends JpaRepository<ObservabilityEntity, UUID> {
}
