package com.citybus.platform.modules.search.service;

import com.citybus.platform.modules.route.entity.RouteEntity;
import com.citybus.platform.modules.route.repository.RouteRepository;
import com.citybus.platform.modules.search.repository.SearchRepository;
import com.citybus.platform.modules.stop.entity.StopEntity;
import com.citybus.platform.modules.stop.repository.StopRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private static final String ROUTE_TYPE = "ROUTE";
    private static final String STOP_TYPE = "STOP";

    private final SearchRepository searchRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;

    @Override
    @Transactional
    public void indexRoute(UUID routeId) {
        RouteEntity route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));

        searchRepository.upsertSearchIndex(
                route.getId(),
                ROUTE_TYPE,
                route.getName(),
                SearchTextNormalizer.toPinyin(route.getName()),
                SearchTextNormalizer.extractInitials(route.getName()),
                route.getFrequencyScore() == null ? null : route.getFrequencyScore().doubleValue(),
                null
        );
    }

    @Override
    @Transactional
    public void indexStop(UUID stopId) {
        StopEntity stop = stopRepository.findById(stopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stop not found"));

        searchRepository.upsertSearchIndex(
                stop.getId(),
                STOP_TYPE,
                stop.getName(),
                SearchTextNormalizer.toPinyin(stop.getName()),
                SearchTextNormalizer.extractInitials(stop.getName()),
                null,
                stop.getPopularityScore() == null ? null : stop.getPopularityScore().doubleValue()
        );
    }

    @Override
    @Transactional
    public void deleteRouteIndex(UUID routeId) {
        searchRepository.deleteByEntity(ROUTE_TYPE, routeId);
    }

    @Override
    @Transactional
    public void deleteStopIndex(UUID stopId) {
        searchRepository.deleteByEntity(STOP_TYPE, stopId);
    }

    @Override
    @Transactional
    public void rebuildFullIndex() {
        searchRepository.deleteAllInBatch();

        for (RouteEntity route : routeRepository.findAll()) {
            searchRepository.upsertSearchIndex(
                    route.getId(),
                    ROUTE_TYPE,
                    route.getName(),
                    SearchTextNormalizer.toPinyin(route.getName()),
                    SearchTextNormalizer.extractInitials(route.getName()),
                    route.getFrequencyScore() == null ? null : route.getFrequencyScore().doubleValue(),
                    null
            );
        }

        for (StopEntity stop : stopRepository.findAll()) {
            searchRepository.upsertSearchIndex(
                    stop.getId(),
                    STOP_TYPE,
                    stop.getName(),
                    SearchTextNormalizer.toPinyin(stop.getName()),
                    SearchTextNormalizer.extractInitials(stop.getName()),
                    null,
                    stop.getPopularityScore() == null ? null : stop.getPopularityScore().doubleValue()
            );
        }
    }
}
