package com.citybus.platform.modules.observability.service;

import com.citybus.platform.modules.observability.dto.ObservabilityDto;
import com.citybus.platform.modules.observability.entity.ObservabilityEntity;
import com.citybus.platform.modules.observability.repository.ObservabilityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObservabilityServiceImpl implements ObservabilityService {

    private final ObservabilityRepository observabilityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ObservabilityDto> listAuditLogs() {
        return observabilityRepository.findAll().stream()
                .map(ObservabilityServiceImpl::toDto)
                .toList();
    }

    private static ObservabilityDto toDto(ObservabilityEntity entity) {
        return new ObservabilityDto(
                entity.getId(),
                entity.getModuleName(),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getCreatedAt()
        );
    }
}
