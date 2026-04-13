package com.citybus.platform.modules.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "data_versions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataEntity {

    @Id
    private UUID id;

    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    @Column(name = "version_label", nullable = false, length = 80)
    private String versionLabel;

    @Column(length = 128)
    private String checksum;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
