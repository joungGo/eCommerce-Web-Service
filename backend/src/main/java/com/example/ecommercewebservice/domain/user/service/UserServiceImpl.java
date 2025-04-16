package com.example.ecommercewebservice.domain.user.service;

import com.example.ecommercewebservice.domain.user.dto.response.UserProfileResponse;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginRequest;
import com.example.ecommercewebservice.domain.user.dto.signIn.LoginResponse;
import com.example.ecommercewebservice.domain.user.dto.signUp.SignupRequest;
import com.example.ecommercewebservice.domain.user.dto.request.UserUpdateRequestDto;
import com.example.ecommercewebservice.domain.user.dto.request.AddressUpdateRequestDto;
import com.example.ecommercewebservice.domain.user.dto.response.UserResponseDto;
import com.example.ecommercewebservice.domain.user.entity.Address;
import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.config.UserRole;
import com.example.ecommercewebservice.domain.user.repository.UserRepository;
import com.example.ecommercewebservice.global.exception.BusinessException;
import com.example.ecommercewebservice.global.exception.ErrorCode;
import com.example.ecommercewebservice.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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

    @Override
    @Transactional
    public User signup(SignupRequest signupRequest) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사용자명 중복 검사
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phoneNumber(signupRequest.getPhoneNumber())
                .username(signupRequest.getUsername())
                .roles(Collections.singletonList(UserRole.USER.getRole()))
                .build();

        // 기본 배송지 생성
        Address defaultAddress = Address.builder()
                .user(user)
                .recipient(signupRequest.getRecipient())
                .postalCode(signupRequest.getPostalCode())
                .address(signupRequest.getAddress())
                .phoneNumber(signupRequest.getAddressPhoneNumber())
                .isDefault(true)
                .build();

        // 사용자와 배송지 저장
        user.getAddresses().add(defaultAddress);
        User savedUser = userRepository.save(user);

        log.info("새로운 사용자의 회원가입: {}", signupRequest.getEmail());
        return savedUser;
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
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getEmail(), e);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    /**
     * 사용자 로그아웃 처리
     * JWT 토큰을 무효화하여 더 이상 사용할 수 없게 함
     *
     * @param token 무효화할 JWT 토큰
     */
    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                log.info("사용자 로그아웃: {}", authentication.getName());
                jwtTokenProvider.invalidateToken(token);
            } catch (Exception e) {
                log.error("로그아웃 실패", e);
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.getRoles().clear();
        user.getRoles().add(role.getRole());
    }

    // 현재 로그인한 사용자의 프로필 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(User user) {
        log.info("사용자 프로필 조회: {}", user.getEmail());
        return UserProfileResponse.from(user); // User를 UserProfileResponse로 변환
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 선택적 필드 업데이트
        if (request.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getUsername() != null) {
            user.updateUsername(request.getUsername());
        }
        if (request.getPhoneNumber() != null) {
            user.updatePhoneNumber(request.getPhoneNumber());
        }

        // 주소 업데이트
        if (request.getAddresses() != null) {
            updateAddresses(user, request.getAddresses());
        }

        return UserResponseDto.from(user);
    }

    public void updateAddresses(User user, List<AddressUpdateRequestDto> addressDtos) {
        List<Address> existingAddresses = user.getAddresses();
        List<Address> newAddresses = new ArrayList<>();

        // 기본 주소 개수 확인
        long defaultAddressCount = addressDtos.stream()
                .filter(AddressUpdateRequestDto::isDefault)
                .count();
        if (defaultAddressCount != 1) {
            throw new BusinessException(ErrorCode.INVALID_DEFAULT_ADDRESS);
        }

        for (AddressUpdateRequestDto dto : addressDtos) {
            if (dto.getAddressId() == null) {
                // 새 주소 추가
                Address newAddress = Address.builder()
                        .recipient(dto.getRecipient())
                        .postalCode(dto.getPostalCode())
                        .address(dto.getAddress())
                        .phoneNumber(dto.getPhone())
                        .isDefault(dto.isDefault())
                        .user(user)
                        .build();
                newAddresses.add(newAddress);
            } else {
                // 기존 주소 수정
                Address existingAddress = existingAddresses.stream()
                        .filter(address -> address.getAddressId().equals(dto.getAddressId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
                
                existingAddress.update(
                        dto.getRecipient(),
                        dto.getPostalCode(),
                        dto.getAddress(),
                        dto.getPhone(),
                        dto.isDefault()
                );
                newAddresses.add(existingAddress);
            }
        }

        // 기존 주소 목록을 새로운 주소 목록으로 교체
        existingAddresses.clear();
        existingAddresses.addAll(newAddresses);
    }
}
