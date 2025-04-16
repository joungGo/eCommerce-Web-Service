package com.example.ecommercewebservice.global.util;

import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.domain.user.repository.UserRepository;
import com.example.ecommercewebservice.global.util.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰의 생성, 검증, 파싱 등을 담당하는 유틸리티 클래스
 * 애플리케이션의 인증 메커니즘에서 토큰 관련 작업을 처리
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private Key key;
    private long tokenValidityInMilliseconds;
    private String issuer;
    private TokenRepository tokenRepository;
    private UserRepository userRepository;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 기본 생성자
     */
    public JwtTokenProvider() {
    }

    /**
     * JWT 토큰 제공자 생성자
     * application.properties에서 JWT 설정 값을 주입받아 초기화
     * 
     * @param secret JWT 서명에 사용되는 비밀 키
     * @param tokenValidityInSeconds 토큰 유효 시간(초)
     * @param issuer JWT 토큰의 발행자
     * @param tokenRepository 토큰 관리 저장소
     */
    @Autowired
    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
        @Value("${jwt.issuer}") String issuer,
        TokenRepository tokenRepository,
        UserRepository userRepository) {
        
        // Base64로 인코딩된 시크릿 키를 디코딩하여 사용
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.issuer = issuer;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;

        log.info("JWT Token Provider initialized with issuer: {}", issuer);
    }

    /**
     * 사용자 인증 정보를 기반으로 JWT 토큰 생성
     * Redis에 토큰을 저장하여 관리
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

        String token = Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            .setIssuer(issuer)
            .setIssuedAt(new Date(now))
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
        
        // Redis에 토큰 저장
        tokenRepository.saveToken(authentication.getName(), token, this.tokenValidityInMilliseconds);
        
        return token;
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

        // Load the actual User entity from database with addresses
        User user = userRepository.findByEmailWithAddresses(claims.getSubject())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + claims.getSubject()));

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    /**
     * JWT 토큰 유효성 검증
     * Redis에서 토큰의 유효성 확인 후 JWT 서명, 만료 여부 검증
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            // Redis에서 토큰 유효성 확인
            if (!tokenRepository.validateToken(token)) {
                log.warn("Redis 또는 Blacklist에 저장된 토큰이 유효하지 않음");
                return false;
            }

            // JWT 서명 및 만료 여부 검증
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            // 토큰 발행자 확인
            if (!claims.getBody().getIssuer().equals(issuer)) {
                log.warn("토큰 발행자와 다릅니다.");
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("JWT 서명 검증 실패");
        } catch (ExpiredJwtException e) {
            log.warn("JWT 토큰 만료");
            // 만료된 토큰은 Redis에서도 제거
            tokenRepository.invalidateToken(token);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid");
        }
        return false;
    }
    
    /**
     * 토큰 무효화 (로그아웃)
     * 
     * @param token 무효화할 JWT 토큰
     */
    public void invalidateToken(String token) {
        tokenRepository.invalidateToken(token);
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출
     * 
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰 또는 null
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * JWT 토큰에서 사용자 역할을 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 역할 목록
     */
    public List<String> getRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String authorities = claims.get("auth", String.class);
        return Arrays.asList(authorities.split(","));
    }
} 