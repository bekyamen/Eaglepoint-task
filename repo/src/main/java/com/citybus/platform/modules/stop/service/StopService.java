package com.citybus.platform.modules.stop.service;

import com.citybus.platform.modules.stop.dto.StopDto;
import java.util.List;
import java.util.UUID;

public interface StopService {
    List<StopDto> listStops();
    StopDto getStop(UUID id);
}
