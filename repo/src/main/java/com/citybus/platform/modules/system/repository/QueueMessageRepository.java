package com.citybus.platform.modules.system.repository;

import com.citybus.platform.modules.system.entity.SystemEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface QueueMessageRepository extends JpaRepository<SystemEntity, UUID> {

    Optional<SystemEntity> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKeyAndStatus(String idempotencyKey, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            SELECT * FROM queue_messages qm
            WHERE qm.status IN ('PENDING', 'FAILED')
              AND COALESCE(qm.next_retry_at, qm.available_at) <= :now
            ORDER BY qm.created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<SystemEntity> lockDueMessages(@Param("now") OffsetDateTime now, @Param("limit") int limit);

    @Query("""
            select q from SystemEntity q
            where q.status = 'PROCESSING'
              and q.lockExpiresAt is not null
              and q.lockExpiresAt <= :now
            """)
    List<SystemEntity> findExpiredLocks(OffsetDateTime now);

    long countByStatus(String status);
}
