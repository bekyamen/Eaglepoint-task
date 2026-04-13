package com.citybus.platform.modules.workflow.service;

import com.citybus.platform.modules.workflow.dto.WorkflowDto;
import java.util.List;

public interface WorkflowService {
    List<WorkflowDto> listStates();
}
