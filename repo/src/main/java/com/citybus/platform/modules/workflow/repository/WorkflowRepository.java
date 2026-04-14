package com.citybus.platform.modules.workflow.repository;

import com.citybus.platform.modules.workflow.entity.WorkflowEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<WorkflowEntity, UUID> {
    Optional<WorkflowEntity> findByWorkflowNameAndCurrentStateIgnoreCase(String workflowName, String currentState);
}
