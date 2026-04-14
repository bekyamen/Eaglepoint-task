package com.citybus.platform.modules.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.citybus.platform.modules.auth.entity.Role;
import com.citybus.platform.modules.auth.entity.User;
import com.citybus.platform.modules.auth.repository.UserRepository;
import com.citybus.platform.modules.observability.repository.ObservabilityRepository;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskHistoryRepository taskHistoryRepository;
    @Mock
    private WorkflowRepository workflowRepository;
    @Mock
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObservabilityRepository observabilityRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void transitionTaskUpdatesStateAndWritesAudit() {
        UUID taskId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        WorkflowEntity pending = WorkflowEntity.builder()
                .id(UUID.randomUUID())
                .workflowName("DISPATCH")
                .currentState("PENDING")
                .allowedTransitions("{}")
                .approvalChainLevel(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        WorkflowEntity approved = WorkflowEntity.builder()
                .id(UUID.randomUUID())
                .workflowName("DISPATCH")
                .currentState("APPROVED")
                .allowedTransitions("{}")
                .approvalChainLevel(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .type("CHECKIN")
                .status("PENDING")
                .workflowState(pending)
                .createdAt(now)
                .updatedAt(now)
                .build();
        User actor = User.builder()
                .id(actorId)
                .username("dispatcher")
                .passwordHash("hash")
                .role(Role.DISPATCHER)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        WorkflowTransitionEntity transition = WorkflowTransitionEntity.builder()
                .id(UUID.randomUUID())
                .workflowState(pending)
                .fromState("PENDING")
                .toState("APPROVED")
                .transitionAction("approve")
                .requiresApproval(true)
                .approvalChainLevel(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(actorId)).thenReturn(Optional.of(actor));
        when(workflowTransitionRepository.findByWorkflowStateWorkflowNameAndFromStateIgnoreCaseAndTransitionActionIgnoreCase(
                "DISPATCH", "PENDING", "approve"
        )).thenReturn(List.of(transition));
        when(workflowRepository.findByWorkflowNameAndCurrentStateIgnoreCase("DISPATCH", "APPROVED"))
                .thenReturn(Optional.of(approved));

        TaskDto result = taskService.transitionTask(
                taskId,
                new TaskTransitionRequest("approve", null, "approved by dispatcher"),
                actorId,
                "trace-42"
        );

        assertEquals("APPROVED", result.status());
        verify(taskHistoryRepository).save(any(TaskHistoryEntity.class));
        verify(observabilityRepository).save(any());
    }

    @Test
    void transitionTaskRejectsInvalidTransition() {
        UUID taskId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        WorkflowEntity pending = WorkflowEntity.builder()
                .id(UUID.randomUUID())
                .workflowName("DISPATCH")
                .currentState("PENDING")
                .allowedTransitions("{}")
                .approvalChainLevel(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .type("CHECKIN")
                .status("PENDING")
                .workflowState(pending)
                .createdAt(now)
                .updatedAt(now)
                .build();
        User actor = User.builder()
                .id(actorId)
                .username("dispatcher")
                .passwordHash("hash")
                .role(Role.DISPATCHER)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(actorId)).thenReturn(Optional.of(actor));
        when(workflowTransitionRepository.findByWorkflowStateWorkflowNameAndFromStateIgnoreCaseAndTransitionActionIgnoreCase(
                "DISPATCH", "PENDING", "escalate"
        )).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.transitionTask(taskId, new TaskTransitionRequest("escalate", null, null), actorId, null)
        );
        assertEquals(409, exception.getStatusCode().value());
    }
}
