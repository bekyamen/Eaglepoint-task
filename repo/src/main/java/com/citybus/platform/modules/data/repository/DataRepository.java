package com.citybus.platform.modules.data.repository;

import com.citybus.platform.modules.data.entity.DataEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<DataEntity, UUID> {
}
