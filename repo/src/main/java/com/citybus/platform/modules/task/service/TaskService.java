package com.citybus.platform.modules.task.service;

import com.citybus.platform.modules.task.dto.TaskDto;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskDto> listTasks(String status);

    TaskDto getTask(UUID taskId);
}
