package com.citybus.platform.modules.task.service;

import com.citybus.platform.modules.auth.entity.User;
import com.citybus.platform.modules.auth.repository.UserRepository;
import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.dto.TaskTransitionRequest;
import com.citybus.platform.modules.task.entity.TaskEntity;
import com.citybus.platform.modules.task.entity.TaskHistoryEntity;
import com.citybus.platform.modules.task.repository.TaskHistoryRepository;
import com.citybus.platform.modules.task.repository.TaskRepository;
import com.citybus.platform.modules.workflow.entity.WorkflowEntity;
import com.citybus.platform.modules.workflow.entity.WorkflowTransitionEntity;
import com.citybus.platform.modules.workflow.repository.WorkflowRepository;
import com.citybus.platform.modules.workflow.repository.WorkflowTransitionRepository;
import com.citybus.platform.modules.observability.entity.ObservabilityEntity;
import com.citybus.platform.modules.observability.repository.ObservabilityRepository;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final UserRepository userRepository;
    private final ObservabilityRepository observabilityRepository;

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

    @Override
    @Transactional
    public TaskDto transitionTask(UUID taskId, TaskTransitionRequest request, UUID actorUserId, String traceId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor user not found"));
        applyTransition(task, request, actor, traceId);
        return toDto(task);
    }

    @Override
    @Transactional
    public List<TaskDto> batchTransition(
            Collection<UUID> taskIds,
            TaskTransitionRequest request,
            UUID actorUserId,
            String traceId
    ) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor user not found"));
        return taskIds.stream()
                .map(taskId -> {
                    TaskEntity task = taskRepository.findById(taskId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + taskId));
                    applyTransition(task, request, actor, traceId);
                    return toDto(task);
                })
                .toList();
    }

    private void applyTransition(TaskEntity task, TaskTransitionRequest request, User actor, String traceId) {
        if (request == null || request.action() == null || request.action().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transition action is required");
        }
        if (task.getWorkflowState() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task is not bound to a workflow state");
        }

        WorkflowEntity currentState = task.getWorkflowState();
        List<WorkflowTransitionEntity> candidates =
                workflowTransitionRepository.findByWorkflowStateWorkflowNameAndFromStateIgnoreCaseAndTransitionActionIgnoreCase(
                        currentState.getWorkflowName(),
                        currentState.getCurrentState(),
                        request.action()
                );
        if (candidates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transition action is not allowed from current state");
        }

        WorkflowTransitionEntity transition = selectTransition(candidates, request.toState());
        WorkflowEntity targetState = workflowRepository
                .findByWorkflowNameAndCurrentStateIgnoreCase(currentState.getWorkflowName(), transition.getToState())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Target workflow state is missing: " + transition.getToState()
                ));

        String fromStatus = task.getStatus();
        task.setWorkflowState(targetState);
        task.setStatus(targetState.getCurrentState());
        task.setUpdatedAt(OffsetDateTime.now());
        taskRepository.save(task);

        TaskHistoryEntity history = TaskHistoryEntity.builder()
                .id(UUID.randomUUID())
                .task(task)
                .action(request.action().toUpperCase())
                .fromStatus(fromStatus)
                .toStatus(task.getStatus())
                .actorUser(actor)
                .notes(request.notes())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        taskHistoryRepository.save(history);

        observabilityRepository.save(ObservabilityEntity.builder()
                .id(UUID.randomUUID())
                .moduleName("WORKFLOW")
                .action("TASK_" + request.action().toUpperCase())
                .actorUser(actor)
                .entityType("TASK")
                .entityId(task.getId())
                .details("{\"fromState\":\"" + fromStatus + "\",\"toState\":\"" + task.getStatus()
                        + "\",\"traceId\":\"" + (traceId == null ? "" : traceId) + "\"}")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build());
    }

    private WorkflowTransitionEntity selectTransition(List<WorkflowTransitionEntity> candidates, String toState) {
        if (toState == null || toState.isBlank()) {
            if (candidates.size() > 1) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Multiple target states available; specify toState"
                );
            }
            return candidates.get(0);
        }
        Optional<WorkflowTransitionEntity> matched = candidates.stream()
                .filter(candidate -> toState.equalsIgnoreCase(candidate.getToState()))
                .findFirst();
        return matched.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Requested toState is not valid for this action"
        ));
    }

    private static TaskDto toDto(TaskEntity entity) {
        return new TaskDto(entity.getId(), entity.getType(), entity.getStatus(), entity.getTimeoutAt());
    }
}
