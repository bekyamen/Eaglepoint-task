package com.citybus.platform.modules.search.service;

import com.citybus.platform.modules.search.dto.SearchDto;
import com.citybus.platform.modules.search.repository.SearchRankProjection;
import com.citybus.platform.modules.search.repository.SearchRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final int SEARCH_LIMIT = 50;
    private static final int AUTOCOMPLETE_LIMIT = 10;
    private static final double SCORE_WEIGHT = 0.2d;

    private final SearchRepository searchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SearchDto> search(String query, String type) {
        String normalizedQuery = SearchTextNormalizer.normalize(query);
        if (normalizedQuery.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query must not be empty");
        }

        String entityType = resolveEntityType(type);
        String prefix = normalizedQuery + "%";
        return searchRepository.search(normalizedQuery, prefix, entityType, SCORE_WEIGHT, PageRequest.of(0, SEARCH_LIMIT))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchDto> autocomplete(String query) {
        String normalizedQuery = SearchTextNormalizer.normalize(query);
        if (normalizedQuery.isBlank()) {
            return List.of();
        }

        String prefix = normalizedQuery + "%";
        return searchRepository.autocomplete(normalizedQuery, prefix, SCORE_WEIGHT, PageRequest.of(0, AUTOCOMPLETE_LIMIT))
                .stream()
                .map(this::toDto)
                .toList();
    }

    private String resolveEntityType(String type) {
        if (type == null || type.isBlank()) {
            return "ALL";
        }
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "route" -> "ROUTE";
            case "stop" -> "STOP";
            case "all" -> "ALL";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid search type");
        };
    }

    private SearchDto toDto(SearchRankProjection row) {
        return SearchDto.builder()
                .entityId(row.getEntityId())
                .entityType(row.getEntityType())
                .name(row.getName())
                .score(row.getScore() == null ? 0d : row.getScore())
                .build();
    }
}
