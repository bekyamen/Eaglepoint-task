package com.citybus.platform.modules.search.repository;

import com.citybus.platform.modules.search.entity.SearchEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchRepository extends JpaRepository<SearchEntity, UUID> {

    Optional<SearchEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    @Query(value = """
            SELECT
                si.id AS id,
                si.entity_id AS entityId,
                si.entity_type AS entityType,
                si.name AS name,
                si.frequency_score AS frequencyScore,
                si.popularity_score AS popularityScore,
                (
                    CASE WHEN lower(si.name) = :q THEN 100 ELSE 0 END +
                    CASE WHEN lower(si.name) LIKE :prefix THEN 70 ELSE 0 END +
                    CASE WHEN si.name_tsv @@ plainto_tsquery('simple', :q) THEN 50 ELSE 0 END +
                    CASE WHEN si.pinyin = :q OR si.pinyin LIKE :prefix THEN 40 ELSE 0 END +
                    CASE WHEN si.initials = :q OR si.initials LIKE :prefix THEN 30 ELSE 0 END +
                    COALESCE(si.frequency_score * :weight, 0) +
                    COALESCE(si.popularity_score * :weight, 0)
                ) AS score
            FROM search_index si
            WHERE (:entityType = 'ALL' OR si.entity_type = :entityType)
              AND (
                    lower(si.name) = :q
                    OR lower(si.name) LIKE :prefix
                    OR si.name_tsv @@ plainto_tsquery('simple', :q)
                    OR si.pinyin LIKE :prefix
                    OR si.initials LIKE :prefix
              )
            ORDER BY score DESC, si.name ASC
            """, nativeQuery = true)
    List<SearchRankProjection> search(@Param("q") String query, @Param("prefix") String prefix,
                                      @Param("entityType") String entityType, @Param("weight") double weight,
                                      Pageable pageable);

    @Query(value = """
            SELECT
                si.id AS id,
                si.entity_id AS entityId,
                si.entity_type AS entityType,
                si.name AS name,
                si.frequency_score AS frequencyScore,
                si.popularity_score AS popularityScore,
                (
                    CASE WHEN lower(si.name) = :q THEN 100 ELSE 0 END +
                    CASE WHEN lower(si.name) LIKE :prefix THEN 70 ELSE 0 END +
                    CASE WHEN si.pinyin = :q OR si.pinyin LIKE :prefix THEN 40 ELSE 0 END +
                    CASE WHEN si.initials = :q OR si.initials LIKE :prefix THEN 30 ELSE 0 END +
                    COALESCE(si.frequency_score * :weight, 0) +
                    COALESCE(si.popularity_score * :weight, 0)
                ) AS score
            FROM search_index si
            WHERE lower(si.name) LIKE :prefix
               OR si.pinyin LIKE :prefix
               OR si.initials LIKE :prefix
            ORDER BY score DESC, si.name ASC
            """, nativeQuery = true)
    List<SearchRankProjection> autocomplete(@Param("q") String query, @Param("prefix") String prefix,
                                            @Param("weight") double weight, Pageable pageable);

    @Modifying
    @Query(value = """
            INSERT INTO search_index (
                id, entity_id, entity_type, name, pinyin, initials, frequency_score, popularity_score, created_at, updated_at
            ) VALUES (
                gen_random_uuid(), :entityId, :entityType, :name, :pinyin, :initials, :frequencyScore, :popularityScore, NOW(), NOW()
            )
            ON CONFLICT (entity_type, entity_id)
            DO UPDATE SET
                name = EXCLUDED.name,
                pinyin = EXCLUDED.pinyin,
                initials = EXCLUDED.initials,
                frequency_score = EXCLUDED.frequency_score,
                popularity_score = EXCLUDED.popularity_score,
                updated_at = NOW()
            """, nativeQuery = true)
    void upsertSearchIndex(@Param("entityId") UUID entityId,
                           @Param("entityType") String entityType,
                           @Param("name") String name,
                           @Param("pinyin") String pinyin,
                           @Param("initials") String initials,
                           @Param("frequencyScore") Double frequencyScore,
                           @Param("popularityScore") Double popularityScore);

    @Modifying
    @Query(value = "DELETE FROM search_index WHERE entity_type = :entityType AND entity_id = :entityId", nativeQuery = true)
    void deleteByEntity(@Param("entityType") String entityType, @Param("entityId") UUID entityId);
}
