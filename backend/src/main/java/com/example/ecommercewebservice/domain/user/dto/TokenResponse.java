package com.example.ecommercewebservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 인증 응답에 사용되는 데이터 전송 객체(DTO)
 * 클라이언트에게 인증 성공 후 JWT 토큰을 반환하기 위한 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /**
     * 인증에 사용되는 JWT 토큰
     * 클라이언트가 보호된 리소스 접근 시 사용
     */
    private String token;

    /**
     * 토큰 타입 (Bearer)
     * HTTP Authorization 헤더에 사용되는 토큰 유형
     */
    private String tokenType = "Bearer"; // Bearer 토큰 타입을 기본값으로 설정
}