package com.citybus.platform.modules.data.service;

import com.citybus.platform.modules.data.dto.DataDto;
import com.citybus.platform.modules.data.dto.IngestionDto;
import com.citybus.platform.modules.data.entity.DataEntity;
import com.citybus.platform.modules.data.entity.RawDataEntity;
import com.citybus.platform.modules.data.repository.DataRepository;
import com.citybus.platform.modules.data.repository.RawDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    private final DataRepository dataRepository;
    private final RawDataRepository rawDataRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DataDto> listVersions() {
        return dataRepository.findAll().stream()
                .map(DataServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngestionDto> listIngestions() {
        return rawDataRepository.findAllByOrderByReceivedAtDesc().stream()
                .map(DataServiceImpl::toIngestionDto)
                .toList();
    }

    private static DataDto toDto(DataEntity entity) {
        return new DataDto(entity.getId(), entity.getSourceName(), entity.getVersionLabel(), entity.isActive());
    }

    private static IngestionDto toIngestionDto(RawDataEntity entity) {
        String versionLabel = entity.getDataVersion() != null ? entity.getDataVersion().getVersionLabel() : "UNVERSIONED";
        return new IngestionDto(
                entity.getId(),
                entity.getSourceName(),
                entity.getIngestStatus(),
                versionLabel,
                entity.getReceivedAt()
        );
    }
}
