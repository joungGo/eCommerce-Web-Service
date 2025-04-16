package com.example.ecommercewebservice.domain.user.dto.signIn;

import com.example.ecommercewebservice.global.constant.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청에 사용되는 데이터 전송 객체(DTO)
 * 클라이언트로부터 로그인 정보를 받기 위한 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 로그인에 사용되는 이메일
     * 사용자 식별에 사용됨
     */
    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.INVALID_EMAIL)
    private String email;

    /**
     * 로그인에 사용되는 비밀번호
     * 사용자 인증에 사용됨
     */
    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    private String password;
}