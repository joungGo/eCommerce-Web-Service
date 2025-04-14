package com.example.ecommercewebservice.domain.user.service;

import com.example.ecommercewebservice.domain.user.dto.LoginRequest;
import com.example.ecommercewebservice.domain.user.dto.LoginResponse;
import com.example.ecommercewebservice.domain.user.dto.SignupRequest;
import com.example.ecommercewebservice.domain.user.dto.TokenResponse;
import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.domain.user.entity.UserRole;
import com.example.ecommercewebservice.domain.user.repository.UserRepository;
import com.example.ecommercewebservice.global.exception.BusinessException;
import com.example.ecommercewebservice.global.exception.ErrorCode;
import com.example.ecommercewebservice.global.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // 사용자 정보 저장소
    private final UserRepository userRepository;

    // 비밀번호 인코더
    private final PasswordEncoder passwordEncoder;

    // 인증 관리자
    private final AuthenticationManager authenticationManager;

    // JWT 토큰 제공자
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 회원가입 기능 구현
     *
     * @param signupRequest 회원가입 요청 정보
     * @return User 생성된 사용자 정보
     * @throws RuntimeException 이미 가입된 이메일인 경우 발생
     */
    @Override
    @Transactional
    public User signup(SignupRequest signupRequest) {

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 새 사용자 생성
        User user = User.builder()
                .email(signupRequest.getEmail())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword())) // 비밀번호 암호화
                .roles(Collections.singletonList(UserRole.USER.getRole()))
                .build();

        return userRepository.save(user);
    }

    /**
     * 로그인 기능 구현 및 JWT 토큰 발급
     * 사용자 인증 후 JWT 토큰 생성
     *
     * @param loginRequest 로그인 요청 정보
     * @return TokenResponse JWT 토큰 응답 객체
     * @throws org.springframework.security.core.AuthenticationException 인증 실패 시 발생
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenProvider.createToken(authentication);
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            return LoginResponse.from(user, token);
        } catch (BadCredentialsException e) {
            log.error("인증 실패: 잘못된 자격 증명");
            throw e;
        } catch (Exception e) {
            log.error("로그인 중 예외 발생");
            throw e;
        }
    }
}
