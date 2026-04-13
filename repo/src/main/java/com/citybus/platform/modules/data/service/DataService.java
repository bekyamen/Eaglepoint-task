package com.citybus.platform.modules.data.service;

import com.citybus.platform.modules.data.dto.DataDto;
import java.util.List;

public interface DataService {
    List<DataDto> listVersions();
}
