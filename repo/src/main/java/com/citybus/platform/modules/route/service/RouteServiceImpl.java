package com.citybus.platform.modules.route.service;

import com.citybus.platform.modules.route.dto.RouteDto;
import com.citybus.platform.modules.route.entity.RouteEntity;
import com.citybus.platform.modules.route.repository.RouteRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RouteDto> listRoutes() {
        return routeRepository.findAll().stream()
                .map(RouteServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDto getRoute(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));
        return toDto(entity);
    }

    private static RouteDto toDto(RouteEntity entity) {
        return new RouteDto(entity.getId(), entity.getName(), entity.getFrequencyScore());
    }
}
