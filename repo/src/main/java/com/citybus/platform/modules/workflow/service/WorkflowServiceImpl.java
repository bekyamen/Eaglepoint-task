package com.citybus.platform.modules.workflow.service;

import com.citybus.platform.modules.workflow.dto.WorkflowDto;
import com.citybus.platform.modules.workflow.dto.WorkflowTransitionDto;
import com.citybus.platform.modules.workflow.entity.WorkflowEntity;
import com.citybus.platform.modules.workflow.entity.WorkflowTransitionEntity;
import com.citybus.platform.modules.workflow.repository.WorkflowRepository;
import com.citybus.platform.modules.workflow.repository.WorkflowTransitionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDto> listStates() {
        return workflowRepository.findAll().stream()
                .map(WorkflowServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTransitionDto> listTransitions(String workflowName, String fromState) {
        return workflowTransitionRepository.findAll().stream()
                .filter(item -> workflowName == null || workflowName.isBlank()
                        || item.getWorkflowState().getWorkflowName().equalsIgnoreCase(workflowName))
                .filter(item -> fromState == null || fromState.isBlank()
                        || item.getFromState().equalsIgnoreCase(fromState))
                .map(WorkflowServiceImpl::toTransitionDto)
                .toList();
    }

    private static WorkflowDto toDto(WorkflowEntity entity) {
        return new WorkflowDto(entity.getId(), entity.getWorkflowName(), entity.getCurrentState());
    }

    private static WorkflowTransitionDto toTransitionDto(WorkflowTransitionEntity entity) {
        return new WorkflowTransitionDto(
                entity.getId(),
                entity.getWorkflowState().getWorkflowName(),
                entity.getFromState(),
                entity.getToState(),
                entity.getTransitionAction(),
                entity.isRequiresApproval(),
                entity.getApprovalChainLevel()
        );
    }
}
