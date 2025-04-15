package com.example.ecommercewebservice.domain.user.dto;

import com.example.ecommercewebservice.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private Long userId;
    private String email;
    private String username;
    private String role;
    private String phoneNumber;
    private String address;
    
    @CreatedDate
    private LocalDateTime createdAt;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getActualUsername())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRoles().getFirst()) // 첫 번째 역할을 사용
                .createdAt(user.getCreatedAt())
                .build();
    }
} 