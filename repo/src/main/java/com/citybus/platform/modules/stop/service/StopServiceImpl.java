package com.citybus.platform.modules.stop.service;

import com.citybus.platform.modules.stop.dto.StopDto;
import com.citybus.platform.modules.stop.entity.StopEntity;
import com.citybus.platform.modules.stop.repository.StopRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class StopServiceImpl implements StopService {

    private final StopRepository stopRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StopDto> listStops() {
        return stopRepository.findAll().stream()
                .map(StopServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StopDto getStop(UUID id) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stop not found"));
        return toDto(entity);
    }

    private static StopDto toDto(StopEntity entity) {
        return new StopDto(entity.getId(), entity.getName(), entity.getPopularityScore());
    }
}
