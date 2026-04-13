package com.citybus.platform.modules.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.citybus.platform.modules.auth.entity.Role;
import com.citybus.platform.modules.auth.entity.User;
import com.citybus.platform.modules.auth.repository.UserRepository;
import com.citybus.platform.modules.notification.entity.NotificationEntity;
import com.citybus.platform.modules.notification.repository.NotificationPreferenceRepository;
import com.citybus.platform.modules.notification.repository.NotificationRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void markAsReadRejectsDifferentOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .username("owner")
                .passwordHash("hash")
                .role(Role.PASSENGER)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        NotificationEntity entity = NotificationEntity.builder()
                .id(notificationId)
                .user(owner)
                .type("ARRIVAL_REMINDER")
                .content("Bus arriving")
                .status("SENT")
                .dedupKey("dedup-1")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(entity));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.markAsRead(notificationId, anotherUserId)
        );

        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void markAllAsReadUpdatesUnreadOnly() {
        UUID userId = UUID.randomUUID();
        User owner = User.builder()
                .id(userId)
                .username("u1")
                .passwordHash("hash")
                .role(Role.PASSENGER)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        NotificationEntity unread = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .user(owner)
                .type("ARRIVAL_REMINDER")
                .content("Unread")
                .status("SENT")
                .dedupKey("dedup-2")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        NotificationEntity alreadyRead = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .user(owner)
                .type("ARRIVAL_REMINDER")
                .content("Read")
                .status("READ")
                .dedupKey("dedup-3")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(notificationRepository.findTop200ByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(java.util.List.of(unread, alreadyRead));

        notificationService.markAllAsRead(userId);

        assertEquals("READ", unread.getStatus());
        assertEquals("READ", alreadyRead.getStatus());
        verify(notificationRepository).saveAll(java.util.List.of(unread, alreadyRead));
    }
}
