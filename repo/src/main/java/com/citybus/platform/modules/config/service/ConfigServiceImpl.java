package com.citybus.platform.modules.config.service;

import com.citybus.platform.modules.config.dto.ConfigDto;
import com.citybus.platform.modules.config.entity.ConfigEntity;
import com.citybus.platform.modules.config.repository.ConfigRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConfigDto> listConfigs() {
        return configRepository.findAll().stream()
                .map(ConfigServiceImpl::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigDto> listTemplates() {
        return listByGroup("template");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigDto> listDictionaries() {
        return listByGroup("dictionary");
    }

    private List<ConfigDto> listByGroup(String group) {
        return configRepository.findByConfigGroupIgnoreCase(group).stream()
                .map(ConfigServiceImpl::toDto)
                .toList();
    }

    private static ConfigDto toDto(ConfigEntity entity) {
        return new ConfigDto(entity.getConfigKey(), entity.getConfigValue(), entity.getConfigGroup());
    }
}
