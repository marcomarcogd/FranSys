package com.fransys.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空") String username,
            @NotBlank(message = "密码不能为空") String password) {
    }

    public record LoginResponse(
            String token,
            Long userId,
            String username,
            String displayName,
            String roleCode,
            String accountLevel,
            Long managerUserId) {
    }

    public record CurrentUserResponse(
            Long userId,
            String username,
            String displayName,
            String roleCode,
            String accountLevel,
            Long managerUserId) {
    }
}
