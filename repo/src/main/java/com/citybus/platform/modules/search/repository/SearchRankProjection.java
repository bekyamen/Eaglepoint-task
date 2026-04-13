package com.citybus.platform.modules.search.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface SearchRankProjection {
    UUID getId();

    UUID getEntityId();

    String getEntityType();

    String getName();

    BigDecimal getFrequencyScore();

    BigDecimal getPopularityScore();

    Double getScore();
}
