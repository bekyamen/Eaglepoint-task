package com.citybus.platform.modules.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search_index")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEntity {

    @Id
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "name_tsv", insertable = false, updatable = false, columnDefinition = "tsvector")
    private String nameTsv;

    @Column(nullable = false, length = 255)
    private String pinyin;

    @Column(nullable = false, length = 64)
    private String initials;

    @Column(name = "frequency_score", precision = 10, scale = 4)
    private BigDecimal frequencyScore;

    @Column(name = "popularity_score", precision = 10, scale = 4)
    private BigDecimal popularityScore;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
