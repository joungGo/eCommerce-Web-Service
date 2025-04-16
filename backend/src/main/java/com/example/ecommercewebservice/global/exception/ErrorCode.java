package com.example.ecommercewebservice.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "요청한 리소스를 찾을 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 오류가 발생했습니다."),
    
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 등록된 이메일입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "이미 사용 중인 사용자명입니다."),
    
    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "P002", "상품의 재고가 부족합니다."),
    
    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O002", "유효하지 않은 주문 상태입니다."),
    
    // Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "장바구니를 찾을 수 없습니다."),
    
    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A004", "인증 정보가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 토큰입니다."),
    
    // User Profile Update
    INVALID_DEFAULT_ADDRESS(HttpStatus.BAD_REQUEST, "U004", "기본 주소는 반드시 하나만 지정해야 합니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "U005", "주소를 찾을 수 없습니다."),
    INVALID_PHONE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "U006", "올바른 전화번호 형식이 아닙니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "U007", "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다."),
    INVALID_POSTAL_CODE_FORMAT(HttpStatus.BAD_REQUEST, "U008", "우편번호는 5자리 숫자여야 합니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
} 