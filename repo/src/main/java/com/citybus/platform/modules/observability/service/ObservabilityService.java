package com.citybus.platform.modules.observability.service;

import com.citybus.platform.modules.observability.dto.ObservabilityDto;
import java.util.List;

public interface ObservabilityService {
    List<ObservabilityDto> listAuditLogs();
}
