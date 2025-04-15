package com.example.ecommercewebservice.domain.token.repository;

import com.example.ecommercewebservice.domain.redis.RedisCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Redis를 사용한 TokenRepository 구현체
 * JWT 토큰을 저장하고 검증하는 기능을 Redis를 통해 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
    private final RedisCommon redisCommon;
    
    private static final String TOKEN_KEY = "auth:token:";
    private static final String BLACKLIST_KEY = "auth:blacklist:";
    
    @Override
    public void saveToken(String username, String token, long expiration) {
        log.debug("사용자 토큰 저장 : {}", username);
        // 밀리초를 초로 변환하여 Duration 객체 생성
        Duration tokenDuration = Duration.ofSeconds(expiration / 1000);
        // RedisCommon의 setDataWithTTL 메서드 활용 (Duration 타입 사용)
        redisCommon.setDataWithTTL(TOKEN_KEY + token, username, tokenDuration);
    }
    
    @Override
    public boolean validateToken(String token) {
        // 토큰이 저장소에 있는지 확인
        String username = redisCommon.getData(TOKEN_KEY + token, String.class);
        // username이 null이거나 비어있으면 유효하지 않은 토큰
        if (username == null || username.isBlank()) {
            log.debug("토큰이 저장소에 존재하지 않음: {}", token.substring(0, Math.min(10, token.length())) + "...");
            return false;
        }
        
        // 블랙리스트에 있는지 확인
        String blacklisted = redisCommon.getData(BLACKLIST_KEY + token, String.class);
        // blacklisted가 null이 아니고 값이 있으면 블랙리스트에 등록된 토큰
        if (blacklisted != null && !blacklisted.isBlank()) {
            log.debug("토큰이 블랙리스트에 존재함: {}", token.substring(0, Math.min(10, token.length())) + "...");
            return false;
        }

        return true;
    }
    
    @Override
    public void invalidateToken(String token) {
        log.debug("토큰 무효화 시작");
        // 기존 토큰 정보 가져오기
        String username = redisCommon.getData(TOKEN_KEY + token, String.class);
        
        if (username != null) {
            // 남은 TTL 확인
            Duration remainingTtl = redisCommon.getRemainingTTL(TOKEN_KEY + token);
            if (remainingTtl != null) {
                // 블랙리스트에 추가 (같은 TTL로 설정)
                redisCommon.setData(BLACKLIST_KEY + token, "revoked");
                redisCommon.setExpireAt(BLACKLIST_KEY + token, 
                    java.time.LocalDateTime.now().plusSeconds(remainingTtl.getSeconds()));
                log.debug("토큰 블랙리스트 등록 완료, TTL: {} 초", remainingTtl.getSeconds());
            }
        }
        
        // 토큰 키 삭제
        redisCommon.getData(TOKEN_KEY + token, String.class); // 삭제 전 확인
        // RedisCommon에 직접적인 삭제 메서드가 없으므로 만료시간을 0으로 설정하여 즉시 만료
        redisCommon.setExpireAt(TOKEN_KEY + token, java.time.LocalDateTime.now());
    }
    
    @Override
    public void deleteUserTokens(String username) {
        log.debug("사용자의 모든 토큰 삭제: {}", username);
        Set<String> allKeys = redisCommon.getAllKeys();
        
        if (allKeys != null) {
            for (String key : allKeys) {
                if (key.startsWith(TOKEN_KEY)) {
                    String tokenKey = key;
                    String storedUsername = redisCommon.getData(tokenKey, String.class);
                    
                    if (username.equals(storedUsername)) {
                        String token = tokenKey.substring(TOKEN_KEY.length());
                        invalidateToken(token);
                    }
                }
            }
        }
    }
} 