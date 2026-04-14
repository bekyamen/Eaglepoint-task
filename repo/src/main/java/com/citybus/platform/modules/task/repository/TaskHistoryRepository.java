package com.citybus.platform.modules.task.repository;

import com.citybus.platform.modules.task.entity.TaskHistoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskHistoryRepository extends JpaRepository<TaskHistoryEntity, UUID> {
}
