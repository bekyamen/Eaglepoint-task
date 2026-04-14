package com.citybus.platform.modules.workflow.repository;

import com.citybus.platform.modules.workflow.entity.WorkflowTransitionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransitionEntity, UUID> {

    List<WorkflowTransitionEntity> findByWorkflowStateWorkflowNameAndFromStateIgnoreCaseAndTransitionActionIgnoreCase(
            String workflowName,
            String fromState,
            String transitionAction
    );
}
