package com.citybus.platform.modules.task.service;

import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.dto.TaskTransitionRequest;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskDto> listTasks(String status);

    TaskDto getTask(UUID taskId);

    TaskDto transitionTask(UUID taskId, TaskTransitionRequest request, UUID actorUserId, String traceId);

    List<TaskDto> batchTransition(Collection<UUID> taskIds, TaskTransitionRequest request, UUID actorUserId, String traceId);
}
