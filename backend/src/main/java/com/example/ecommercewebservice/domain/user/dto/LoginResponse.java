package com.example.ecommercewebservice.domain.user.dto;

import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private String email;
    private String username;
    private String role;
    private String accessToken;

    public static LoginResponse from(User user, String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getActualUsername())
                .role(user.getRoles().stream()
                        .filter(role -> role.equals(UserRole.USER.getRole()))
                        .findFirst()
                        .orElse(user.getRoles().isEmpty() ? UserRole.USER.getRole() : user.getRoles().getFirst()))
                .build();
    }
} 