package com.citybus.platform.modules.route.repository;

import com.citybus.platform.modules.route.entity.RouteEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<RouteEntity, UUID> {
}
