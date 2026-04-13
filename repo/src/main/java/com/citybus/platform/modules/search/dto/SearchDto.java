package com.citybus.platform.modules.search.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    private UUID entityId;
    private String entityType;
    private String name;
    private double score;
}
