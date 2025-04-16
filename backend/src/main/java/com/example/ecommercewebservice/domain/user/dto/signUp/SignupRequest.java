package com.example.ecommercewebservice.domain.user.dto.signUp;

import com.example.ecommercewebservice.global.constant.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 2, max = 20, message = MessageConstants.INVALID_USERNAME_LENGTH)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = MessageConstants.INVALID_USERNAME_PATTERN)
    private String username;

    /**
     * 회원가입에 사용되는 비밀번호
     * 계정 인증에 사용됨
     */
    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    @Size(min = 8, max = 20, message = MessageConstants.INVALID_PASSWORD_LENGTH)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = MessageConstants.INVALID_PASSWORD_PATTERN
    )
    private String password;

    /**
     * 회원가입에 사용되는 전화번호
     * 사용자 계정의 추가 정보로 사용됨
     */
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = MessageConstants.INVALID_PHONE_NUMBER)
    private String phoneNumber;

    /**
     * 회원가입 시 기본 배송지 정보
     */
    @NotBlank(message = MessageConstants.RECIPIENT_REQUIRED)
    private String recipient;  // 수령인 이름

    @NotBlank(message = MessageConstants.POSTAL_CODE_REQUIRED)
    @Pattern(regexp = "^\\d{5}$", message = MessageConstants.INVALID_POSTAL_CODE)
    private String postalCode;  // 우편번호

    @NotBlank(message = MessageConstants.ADDRESS_REQUIRED)
    @Size(max = 200, message = MessageConstants.INVALID_ADDRESS_LENGTH)
    private String address;  // 주소

    @NotBlank(message = MessageConstants.ADDRESS_PHONE_REQUIRED)
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = MessageConstants.INVALID_PHONE_NUMBER)
    private String addressPhoneNumber;  // 배송지 전화번호
} 