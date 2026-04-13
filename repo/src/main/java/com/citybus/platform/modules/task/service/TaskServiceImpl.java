package com.citybus.platform.modules.task.service;

import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.entity.TaskEntity;
import com.citybus.platform.modules.task.repository.TaskRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> listTasks(String status) {
        return taskRepository.findAll().stream()
                .filter(task -> status == null || status.isBlank() || status.equalsIgnoreCase(task.getStatus()))
                .map(TaskServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTask(UUID taskId) {
        return taskRepository.findById(taskId)
                .map(TaskServiceImpl::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private static TaskDto toDto(TaskEntity entity) {
        return new TaskDto(entity.getId(), entity.getType(), entity.getStatus(), entity.getTimeoutAt());
    }
}
