package com.citybus.platform.modules.config.repository;

import com.citybus.platform.modules.config.entity.ConfigEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigEntity, UUID> {
    List<ConfigEntity> findByConfigGroupIgnoreCase(String configGroup);
}
