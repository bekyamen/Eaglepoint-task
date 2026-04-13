package com.citybus.platform.modules.data.service;

import com.citybus.platform.modules.data.dto.DataDto;
import com.citybus.platform.modules.data.entity.DataEntity;
import com.citybus.platform.modules.data.repository.DataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    private final DataRepository dataRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DataDto> listVersions() {
        return dataRepository.findAll().stream()
                .map(DataServiceImpl::toDto)
                .toList();
    }

    private static DataDto toDto(DataEntity entity) {
        return new DataDto(entity.getId(), entity.getSourceName(), entity.getVersionLabel(), entity.isActive());
    }
}
