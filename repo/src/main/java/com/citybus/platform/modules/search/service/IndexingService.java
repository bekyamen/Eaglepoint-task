package com.citybus.platform.modules.search.service;

import java.util.UUID;

public interface IndexingService {
    void indexRoute(UUID routeId);

    void indexStop(UUID stopId);

    void deleteRouteIndex(UUID routeId);

    void deleteStopIndex(UUID stopId);

    void rebuildFullIndex();
}
