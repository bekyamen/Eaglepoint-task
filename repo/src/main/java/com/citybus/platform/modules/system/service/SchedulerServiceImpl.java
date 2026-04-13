package com.citybus.platform.modules.system.service;

import com.citybus.platform.modules.notification.service.NotificationService;
import com.citybus.platform.modules.system.dto.QueueNotificationPayload;
import com.citybus.platform.modules.system.entity.SchedulerStateEntity;
import com.citybus.platform.modules.system.entity.SystemEntity;
import com.citybus.platform.modules.system.repository.SchedulerStateRepository;
import com.citybus.platform.modules.task.entity.TaskEntity;
import com.citybus.platform.modules.task.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    private static final String SCHEDULER_KEY = "main-scheduler";
    private static final int BATCH_SIZE = 100;

    private final QueueService queueService;
    private final NotificationService notificationService;
    private final NotificationProcessor notificationProcessor;
    private final SchedulerStateRepository schedulerStateRepository;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void recoverOnStartup() {
        runSchedulerCycle();
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void runSchedulerCycle() {
        OffsetDateTime now = OffsetDateTime.now();
        upsertSchedulerState(now, "RUNNING");

        queueService.recoverExpiredLocks();
        enqueueMissedCheckinAlerts(now);

        List<SystemEntity> dueMessages = queueService.fetchAndLockPendingMessages(BATCH_SIZE);
        for (SystemEntity message : dueMessages) {
            try {
                notificationProcessor.process(message);
                queueService.markDone(message.getId());
            } catch (Exception ex) {
                log.warn("Queue processing failed for message {}", message.getId(), ex);
                queueService.markFailedWithRetry(message.getId(), ex.getMessage());
            }
        }

        notificationService.triggerPendingNotifications(now);
        upsertSchedulerState(now, "IDLE");
    }

    private void enqueueMissedCheckinAlerts(OffsetDateTime now) {
        List<TaskEntity> overdueTasks = taskRepository.findOverdueTasks(now);
        for (TaskEntity task : overdueTasks) {
            if (task.getAssignedTo() == null) {
                continue;
            }
            String idempotencyKey = "missed-checkin:" + task.getId();
            QueueNotificationPayload payload = QueueNotificationPayload.builder()
                    .userId(task.getAssignedTo().getId())
                    .type("MISSED_CHECKIN_ALERT")
                    .content("Missed check-in for task " + task.getId())
                    .eventTime(task.getTimeoutAt() == null ? now : task.getTimeoutAt())
                    .dedupKey("notification:" + idempotencyKey)
                    .build();
            queueService.enqueueMessage("NOTIFICATION_EVENT", toJson(payload), now, idempotencyKey);
        }
    }

    private void upsertSchedulerState(OffsetDateTime now, String status) {
        SchedulerStateEntity state = schedulerStateRepository.findBySchedulerKey(SCHEDULER_KEY)
                .orElseGet(() -> SchedulerStateEntity.builder()
                        .id(UUID.randomUUID())
                        .schedulerKey(SCHEDULER_KEY)
                        .createdAt(now)
                        .build());

        state.setStatus(status);
        state.setLastRunAt(now);
        state.setNextRunAt(now.plusSeconds(60));
        state.setUpdatedAt(now);
        schedulerStateRepository.save(state);
    }

    private String toJson(QueueNotificationPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize queue payload", ex);
        }
    }
}
