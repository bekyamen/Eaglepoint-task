package com.citybus.platform.modules.stop.repository;

import com.citybus.platform.modules.stop.entity.StopEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<StopEntity, UUID> {
}
