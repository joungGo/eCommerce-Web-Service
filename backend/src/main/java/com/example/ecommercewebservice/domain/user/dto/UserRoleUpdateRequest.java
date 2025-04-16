package com.example.ecommercewebservice.domain.user.dto;

import com.example.ecommercewebservice.config.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 권한 업데이트 요청 DTO
 * 
 * 사용 위치:
 * - AdminController - 관리자가 사용자 권한을 변경할 때 사용
 * - UserService.updateUserRole() - 사용자 권한 변경 비즈니스 로직에서 사용
 * 
 * 용도:
 * - 관리자가 다른 사용자의 역할/권한을 변경하기 위한 요청 데이터를 담는 객체
 * - 사용자 권한 관리를 위한 관리자 전용 기능에서 사용
 */
@Getter
@NoArgsConstructor
public class UserRoleUpdateRequest {
    /**
     * 변경할 사용자 역할 (ROLE_USER, ROLE_ADMIN 등)
     */
    private UserRole role;
} 