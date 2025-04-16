package com.example.ecommercewebservice.global.util.repository;

/**
 * JWT 토큰 관리를 위한 저장소 인터페이스
 * Redis를 통해 토큰을 저장하고 검증하는 기능 제공
 */
public interface TokenRepository {
    /**
     * 사용자 토큰을 저장
     * 
     * @param username 사용자 식별자
     * @param token JWT 토큰
     * @param expiration 만료 시간(밀리초)
     */
    void saveToken(String username, String token, long expiration);
    
    /**
     * 토큰 유효성 검증
     * 
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 블랙리스트에 있거나 존재하지 않으면 false
     */
    boolean validateToken(String token);
    
    /**
     * 토큰 무효화 (블랙리스트에 추가)
     * 
     * @param token 무효화할 JWT 토큰
     */
    void invalidateToken(String token);
    
    /**
     * 사용자의 모든 토큰 삭제
     * 
     * @param username 사용자 식별자
     */
    void deleteUserTokens(String username);
} 