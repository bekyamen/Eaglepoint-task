package com.citybus.platform.modules.system.service;

import com.citybus.platform.modules.notification.service.NotificationService;
import com.citybus.platform.modules.system.dto.QueueNotificationPayload;
import com.citybus.platform.modules.system.entity.SystemEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationProcessorImpl implements NotificationProcessor {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Override
    public void process(SystemEntity message) {
        QueueNotificationPayload payload = parsePayload(message.getPayload());
        String dedupKey = payload.getDedupKey() == null || payload.getDedupKey().isBlank()
                ? message.getIdempotencyKey()
                : payload.getDedupKey();
        notificationService.createNotification(
                payload.getUserId(),
                payload.getType(),
                payload.getContent(),
                notificationService.scheduleNotification(payload.getType(), payload.getEventTime()),
                dedupKey
        );
    }

    private QueueNotificationPayload parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, QueueNotificationPayload.class);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid queue payload");
        }
    }
}
