package com.example.ecommercewebservice.domain.user.dto;

import com.example.ecommercewebservice.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRoleUpdateRequest {
    private UserRole role;
} 