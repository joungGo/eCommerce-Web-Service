package com.example.ecommercewebservice.domain.admin.controller;

import com.example.ecommercewebservice.domain.user.dto.UserRoleUpdateRequest;
import com.example.ecommercewebservice.domain.user.service.UserService;
import com.example.ecommercewebservice.global.security.annotation.RoleRequired;
import com.example.ecommercewebservice.config.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RoleRequired(UserRole.ADMIN)
public class AdminController {

    private final UserService userService;

    /**
     * 사용자 역할 변경 API
     * 관리자만 접근 가능
     *
     * @param userId 변경할 사용자 ID
     * @param request 역할 변경 요청
     * @return 성공/실패 응답
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest request) {
        userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자 대시보드 API
     * 관리자만 접근 가능
     *
     * @return 대시보드 데이터
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        // TODO: 대시보드 데이터 조회 로직 구현
        return ResponseEntity.ok().build();
    }
} 