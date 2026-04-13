package com.citybus.platform.modules.route.service;

import com.citybus.platform.modules.route.dto.RouteDto;
import java.util.List;
import java.util.UUID;

public interface RouteService {
    List<RouteDto> listRoutes();
    RouteDto getRoute(UUID id);
}
