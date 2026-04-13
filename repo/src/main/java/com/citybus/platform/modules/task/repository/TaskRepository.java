package com.citybus.platform.modules.task.repository;

import com.citybus.platform.modules.task.entity.TaskEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("""
            select t from TaskEntity t
            where t.timeoutAt is not null
              and t.timeoutAt <= :now
              and t.status not in ('DONE', 'FAILED')
            """)
    List<TaskEntity> findOverdueTasks(OffsetDateTime now);
}
