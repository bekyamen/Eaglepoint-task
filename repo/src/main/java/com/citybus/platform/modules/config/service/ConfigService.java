package com.citybus.platform.modules.config.service;

import com.citybus.platform.modules.config.dto.ConfigDto;
import java.util.List;

public interface ConfigService {
    List<ConfigDto> listConfigs();

    List<ConfigDto> listTemplates();

    List<ConfigDto> listDictionaries();
}
