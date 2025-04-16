package com.example.ecommercewebservice.domain.user.dto.profile;

import com.example.ecommercewebservice.domain.user.dto.adress.AddressResponse;
import com.example.ecommercewebservice.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String username;
    private String phoneNumber;
    private List<AddressResponse> addresses;
    private String role;
    private LocalDateTime createdAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getActualUsername())
                .phoneNumber(user.getPhoneNumber())
                .addresses(user.getAddresses().stream()
                        .map(AddressResponse::from)
                        .collect(Collectors.toList()))
                .role(user.getRoles().getFirst())
                .createdAt(user.getCreatedAt())
                .build();
    }
}