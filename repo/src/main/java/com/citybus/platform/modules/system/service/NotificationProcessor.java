package com.citybus.platform.modules.system.service;

import com.citybus.platform.modules.system.entity.SystemEntity;

public interface NotificationProcessor {
    void process(SystemEntity message);
}
