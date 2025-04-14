package com.example.ecommercewebservice.global.exception;

import com.example.ecommercewebservice.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RsData<String>> handleBusinessException(BusinessException e) {
        RsData<String> rsData = new RsData<>(e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(rsData);
    }

    // 로그인 실패 예외 처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RsData<String>> handleBadCredentialsException(BadCredentialsException e) {
        RsData<String> rsData = new RsData<>(ErrorCode.INVALID_PASSWORD.getCode(), "이메일 또는 비밀번호가 올바르지 않습니다.");
        return ResponseEntity.status(ErrorCode.INVALID_PASSWORD.getStatus()).body(rsData);
    }

    // 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<String>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        RsData<String> rsData = new RsData<>(ErrorCode.INVALID_INPUT.getCode(), errorMessage);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus()).body(rsData);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<RsData<String>> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        RsData<String> rsData = new RsData<>(ErrorCode.INVALID_INPUT.getCode(), errorMessage);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus()).body(rsData);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<String>> handleException(Exception e) {
        RsData<String> rsData = new RsData<>(ErrorCode.SERVER_ERROR.getCode(), "서버 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(ErrorCode.SERVER_ERROR.getStatus()).body(rsData);
    }
} 