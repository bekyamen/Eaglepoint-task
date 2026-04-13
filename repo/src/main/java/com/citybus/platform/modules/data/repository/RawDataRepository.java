package com.citybus.platform.modules.data.repository;

import com.citybus.platform.modules.data.entity.RawDataEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepository extends JpaRepository<RawDataEntity, UUID> {
    List<RawDataEntity> findAllByOrderByReceivedAtDesc();
}
