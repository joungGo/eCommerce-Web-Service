package com.example.ecommercewebservice.domain.user.service;

import com.example.ecommercewebservice.domain.user.dto.profile.UserProfileResponse;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginRequest;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginResponse;
import com.example.ecommercewebservice.domain.user.dto.signUp.SignupRequest;
import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.config.UserRole;

public interface UserService {
    /**
     * 회원가입 처리
     *
     * @param signupRequest 회원가입 요청 정보
     * @return User 생성된 사용자 정보
     */
    User signup(SignupRequest signupRequest);

    /**
     * 로그인 처리 및 JWT 토큰 발급
     *
     * @param loginRequest 로그인 요청 정보
     * @return LoginResponse 로그인 응답 객체 (JWT 토큰 및 사용자 정보)
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 사용자 로그아웃 처리
     * 
     * @param token 무효화할 JWT 토큰
     */
    void logout(String token);

    /**
     * 사용자 역할 변경
     * 관리자만 호출 가능
     *
     * @param userId 변경할 사용자 ID
     * @param role 새로운 역할
     */
    void updateUserRole(Long userId, UserRole role);

    /**
     * 현재 로그인한 사용자의 프로필 정보 조회
     *
     * @param user 현재 로그인한 사용자
     * @return UserProfileResponse 사용자 프로필 정보
     */
    UserProfileResponse getMyProfile(User user);
}
