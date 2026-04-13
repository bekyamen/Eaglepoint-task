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
@Table(name = "parsed_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedDataEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_data_id")
    private RawDataEntity rawData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_version_id")
    private DataEntity dataVersion;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "parsed_payload", nullable = false, columnDefinition = "jsonb")
    private String parsedPayload;

    @Column(name = "validation_status", nullable = false, length = 30)
    private String validationStatus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
