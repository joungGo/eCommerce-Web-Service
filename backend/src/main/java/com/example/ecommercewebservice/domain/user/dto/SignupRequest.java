package com.example.ecommercewebservice.domain.user.dto;

import com.example.ecommercewebservice.global.constant.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 요청에 사용되는 데이터 전송 객체(DTO)
 * 클라이언트로부터 회원가입 정보를 받기 위한 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    /**
     * 회원가입에 사용되는 이메일
     * 사용자 계정의 고유 식별자로 사용됨
     */
    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.INVALID_EMAIL)
    private String email;

    /**
     * 회원가입에 사용되는 사용자 이름
     * 사용자 프로필에 표시되는 이름
     */
    @NotBlank(message = MessageConstants.USERNAME_REQUIRED)
    // TODO-검증: 특수문자 사용 불가능
    private String username;

    /**
     * 회원가입에 사용되는 비밀번호
     * 계정 인증에 사용됨
     * 최소 6자 이상의 길이가 요구됨
     */
    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    @Size(min = 5, message = MessageConstants.INVALID_PASSWORD)
    // TODO-검증: 소문자 + 숫자 조합이어야 한다.
    private String password;
} 