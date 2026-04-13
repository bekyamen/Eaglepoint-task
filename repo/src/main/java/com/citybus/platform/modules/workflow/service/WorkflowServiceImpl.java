package com.citybus.platform.modules.workflow.service;

import com.citybus.platform.modules.workflow.dto.WorkflowDto;
import com.citybus.platform.modules.workflow.entity.WorkflowEntity;
import com.citybus.platform.modules.workflow.repository.WorkflowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDto> listStates() {
        return workflowRepository.findAll().stream()
                .map(WorkflowServiceImpl::toDto)
                .toList();
    }

    private static WorkflowDto toDto(WorkflowEntity entity) {
        return new WorkflowDto(entity.getId(), entity.getWorkflowName(), entity.getCurrentState());
    }
}
