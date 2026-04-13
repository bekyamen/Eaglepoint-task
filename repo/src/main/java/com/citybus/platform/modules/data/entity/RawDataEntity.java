package com.citybus.platform.modules.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "raw_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawDataEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_version_id")
    private DataEntity dataVersion;

    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "ingest_status", nullable = false, length = 30)
    private String ingestStatus;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
