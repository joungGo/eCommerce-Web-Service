package com.example.ecommercewebservice.global.constant;

/**
 * 애플리케이션 전역에서 사용되는 메시지 상수를 정의하는 클래스
 */
public class MessageConstants {
    
    // Common
    public static final String SUCCESS = "성공";
    public static final String FAIL = "실패";
    
    // User
    public static final String EMAIL_REQUIRED = "이메일은 필수 입력값입니다.";
    public static final String INVALID_EMAIL = "올바른 이메일 형식이 아닙니다.";
    public static final String USERNAME_REQUIRED = "사용자 이름은 필수 입력값입니다.";
    public static final String INVALID_USERNAME_LENGTH = "사용자 이름은 2자 이상 20자 이하여야 합니다.";
    public static final String INVALID_USERNAME_PATTERN = "사용자 이름은 영문, 숫자, 한글만 사용 가능합니다.";
    public static final String PASSWORD_REQUIRED = "비밀번호는 필수 입력값입니다.";
    public static final String INVALID_PASSWORD_LENGTH = "비밀번호는 8자 이상 20자 이하여야 합니다.";
    public static final String INVALID_PASSWORD_PATTERN = "비밀번호는 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다.";
    public static final String INVALID_PHONE_NUMBER = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)";
    public static final String INVALID_ADDRESS_LENGTH = "주소는 200자 이하여야 합니다.";
    
    // Auth
    public static final String SIGNUP_SUCCESS = "회원가입이 완료되었습니다.";
    public static final String LOGIN_SUCCESS = "로그인이 완료되었습니다.";
    public static final String LOGOUT_SUCCESS = "로그아웃이 완료되었습니다.";
    public static final String TOKEN_EXPIRED = "만료된 토큰입니다.";
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String ACCESS_DENIED = "접근 권한이 없습니다.";
} 