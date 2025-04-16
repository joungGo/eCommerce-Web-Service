package com.example.ecommercewebservice.domain.user.controller;

import com.example.ecommercewebservice.config.UserRole;
import com.example.ecommercewebservice.domain.user.dto.profile.UserProfileResponse;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginRequest;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginResponse;
import com.example.ecommercewebservice.domain.user.dto.signUp.SignupRequest;
import com.example.ecommercewebservice.domain.user.dto.signUp.SignupResponse;
import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.domain.user.service.UserService;
import com.example.ecommercewebservice.global.constant.MessageConstants;
import com.example.ecommercewebservice.global.dto.RsData;
import com.example.ecommercewebservice.global.security.annotation.RoleRequired;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 사용자 회원가입
    @PostMapping("/signup")
    public ResponseEntity<RsData<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        // 회원가입 로직 구현 - 예외는 GlobalExceptionHandler에서 처리
        User user = userService.signup(signupRequest);

        // 성공 시 - 사용자 정보를 UserResponse 객체로 변환하여 반환
        SignupResponse signupResponse = SignupResponse.from(user);
        RsData<SignupResponse> rsData = new RsData<>(String.valueOf(HttpStatus.OK.value()), MessageConstants.SIGNUP_SUCCESS, signupResponse);
        return ResponseEntity.ok(rsData);
    }

    // 사용자 로그인
    @PostMapping("/login")
    public ResponseEntity<RsData<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 로그인 로직 구현 - 예외는 GlobalExceptionHandler에서 처리
        LoginResponse loginResponse = userService.login(loginRequest);

        // 성공 시 - 로그인 응답 반환
        RsData<LoginResponse> rsData = new RsData<>(String.valueOf(HttpStatus.OK.value()), MessageConstants.LOGIN_SUCCESS, loginResponse);
        return ResponseEntity.ok(rsData);
    }

    // 사용자 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token);
            return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 인증 정보입니다."));
    }

    // 사용자 프로필(정보) 조회
    @GetMapping("/me")
    @RoleRequired(UserRole.USER) // USER 권한이 있는 사용자만 접근 가능
    public ResponseEntity<RsData<UserProfileResponse>> getMyProfile(@AuthenticationPrincipal User user) { // @AuthenticationPrincipal 어노테이션을 사용하여 현재 로그인한 사용자 정보를 가져와서 User에 주입, 메서드 파라미터에만 사용할 수 있다.
        UserProfileResponse profile = userService.getMyProfile(user);
        RsData<UserProfileResponse> rsData = new RsData<>(
                String.valueOf(HttpStatus.OK.value()),
                "프로필 조회 성공",
                profile
        );
        return ResponseEntity.ok(rsData);
    }

    // 사용자 프로필(정보) 수정
}
