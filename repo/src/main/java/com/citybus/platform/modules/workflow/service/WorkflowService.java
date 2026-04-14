package com.citybus.platform.modules.workflow.service;

import com.citybus.platform.modules.workflow.dto.WorkflowDto;
import com.citybus.platform.modules.workflow.dto.WorkflowTransitionDto;
import java.util.List;

public interface WorkflowService {
    List<WorkflowDto> listStates();

    List<WorkflowTransitionDto> listTransitions(String workflowName, String fromState);
}
