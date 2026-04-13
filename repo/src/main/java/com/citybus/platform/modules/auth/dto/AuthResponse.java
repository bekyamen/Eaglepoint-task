package com.citybus.platform.modules.auth.dto;

import com.citybus.platform.modules.auth.entity.Role;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private UserPayload user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPayload {
        private UUID id;
        private String username;
        private Role role;
    }
}
