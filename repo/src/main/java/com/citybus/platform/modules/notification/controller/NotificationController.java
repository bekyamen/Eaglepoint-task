package com.citybus.platform.modules.notification.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.auth.security.UserPrincipal;
import com.citybus.platform.modules.notification.dto.NotificationDto;
import com.citybus.platform.modules.notification.entity.NotificationEntity;
import com.citybus.platform.modules.notification.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> list(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<NotificationDto> items = notificationService.listForUser(principal.getId()).stream()
                .map(NotificationController::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.<List<NotificationDto>>builder()
                .success(true)
                .data(items)
                .build());
    }

    @PostMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NotificationEntity updated = notificationService.markAsRead(notificationId, principal.getId());
        return ResponseEntity.ok(ApiResponse.<NotificationDto>builder()
                .success(true)
                .data(toDto(updated))
                .build());
    }

    @PostMapping("/read-all")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }

    private static NotificationDto toDto(NotificationEntity entity) {
        return new NotificationDto(
                entity.getId(),
                entity.getType(),
                entity.getContent(),
                entity.getStatus(),
                entity.getScheduledTime(),
                entity.getCreatedAt()
        );
    }
}
