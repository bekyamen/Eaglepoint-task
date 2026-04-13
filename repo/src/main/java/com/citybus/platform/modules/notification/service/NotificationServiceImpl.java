package com.citybus.platform.modules.notification.service;

import com.citybus.platform.modules.auth.entity.User;
import com.citybus.platform.modules.auth.repository.UserRepository;
import com.citybus.platform.modules.notification.entity.NotificationEntity;
import com.citybus.platform.modules.notification.entity.NotificationPreferenceEntity;
import com.citybus.platform.modules.notification.repository.NotificationPreferenceRepository;
import com.citybus.platform.modules.notification.repository.NotificationRepository;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEntity> listForUser(UUID userId) {
        return notificationRepository.findTop200ByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public NotificationEntity createNotification(UUID userId, String type, String content, OffsetDateTime scheduledTime, String dedupKey) {
        notificationRepository.findByDedupKey(dedupKey).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate notification dedup key");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime effectiveSchedule = applyDnd(userId, scheduledTime == null ? now : scheduledTime);

        NotificationEntity entity = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .type(type)
                .content(content)
                .status("PENDING")
                .scheduledTime(effectiveSchedule)
                .dedupKey(dedupKey)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return notificationRepository.save(entity);
    }

    @Override
    public OffsetDateTime scheduleNotification(String type, OffsetDateTime eventTime) {
        if (eventTime == null) {
            return OffsetDateTime.now();
        }
        return switch (type) {
            case "ARRIVAL_REMINDER" -> eventTime.minusMinutes(10);
            case "MISSED_CHECKIN_ALERT" -> eventTime.plusMinutes(5);
            default -> eventTime;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public OffsetDateTime applyDnd(UUID userId, OffsetDateTime candidateTime) {
        NotificationPreferenceEntity pref = preferenceRepository.findByUserId(userId).orElse(null);
        if (pref == null || !pref.isDndEnabled() || pref.getDndStart() == null || pref.getDndEnd() == null) {
            return candidateTime;
        }

        LocalTime candidateLocal = candidateTime.toLocalTime();
        LocalTime dndStart = pref.getDndStart();
        LocalTime dndEnd = pref.getDndEnd();

        boolean isWithinDnd;
        if (dndStart.isBefore(dndEnd) || dndStart.equals(dndEnd)) {
            isWithinDnd = !candidateLocal.isBefore(dndStart) && candidateLocal.isBefore(dndEnd);
        } else {
            isWithinDnd = !candidateLocal.isBefore(dndStart) || candidateLocal.isBefore(dndEnd);
        }

        if (!isWithinDnd) {
            return candidateTime;
        }

        OffsetDateTime delayed = candidateTime.withHour(dndEnd.getHour()).withMinute(dndEnd.getMinute()).withSecond(dndEnd.getSecond());
        if (!delayed.isAfter(candidateTime)) {
            delayed = delayed.plusDays(1);
        }
        return delayed;
    }

    @Override
    @Transactional
    public NotificationEntity markAsRead(UUID notificationId, UUID userId) {
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!entity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        entity.setStatus("READ");
        entity.setUpdatedAt(OffsetDateTime.now());
        return notificationRepository.save(entity);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<NotificationEntity> items = notificationRepository.findTop200ByUserIdOrderByCreatedAtDesc(userId);
        OffsetDateTime now = OffsetDateTime.now();
        for (NotificationEntity item : items) {
            if (!"READ".equals(item.getStatus())) {
                item.setStatus("READ");
                item.setUpdatedAt(now);
            }
        }
        notificationRepository.saveAll(items);
    }

    @Override
    @Transactional
    public List<NotificationEntity> triggerPendingNotifications(OffsetDateTime now) {
        List<NotificationEntity> due = notificationRepository
                .findTop100ByStatusAndScheduledTimeLessThanEqualOrderByScheduledTimeAsc("PENDING", now);
        for (NotificationEntity item : due) {
            item.setStatus("SENT");
            item.setSentAt(now);
            item.setUpdatedAt(now);
        }
        return notificationRepository.saveAll(due);
    }
}
