package com.citybus.platform.modules.data.service;

import com.citybus.platform.modules.data.dto.DataDto;
import com.citybus.platform.modules.data.dto.IngestionDto;
import java.util.List;

public interface DataService {
    List<DataDto> listVersions();

    List<IngestionDto> listIngestions();
}
