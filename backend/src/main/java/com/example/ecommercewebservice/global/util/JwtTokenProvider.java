package com.example.ecommercewebservice.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰의 생성, 검증, 파싱 등을 담당하는 유틸리티 클래스
 * 애플리케이션의 인증 메커니즘에서 토큰 관련 작업을 처리
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long tokenValidityInMilliseconds;
    private final String issuer;

    /**
     * JWT 토큰 제공자 생성자
     * application.properties에서 JWT 설정 값을 주입받아 초기화
     * 
     * @param secret JWT 서명에 사용되는 비밀 키
     * @param tokenValidityInSeconds 토큰 유효 시간(초)
     * @param issuer JWT 토큰의 발행자
     */
    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
        @Value("${jwt.issuer}") String issuer) {
        
        // Base64로 인코딩된 시크릿 키를 디코딩하여 사용
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.issuer = issuer;
        
        log.info("JWT Token Provider initialized with issuer: {}", issuer);
    }

    /**
     * 사용자 인증 정보를 기반으로 JWT 토큰 생성
     * 
     * @param authentication 인증된 사용자 정보
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            .setIssuer(issuer)
            .setIssuedAt(new Date(now))
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * JWT 토큰에서 인증 정보 추출
     * 
     * @param token JWT 토큰
     * @return Spring Security Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            // 토큰 발행자 확인
            if (!claims.getBody().getIssuer().equals(issuer)) {
                log.warn("Invalid JWT issuer");
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid");
        }
        return false;
    }
} 