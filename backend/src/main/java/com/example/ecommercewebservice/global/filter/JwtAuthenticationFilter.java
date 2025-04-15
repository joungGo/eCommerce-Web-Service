package com.example.ecommercewebservice.global.filter;

import com.example.ecommercewebservice.global.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 토큰 기반 인증을 처리하는 필터
 * 모든 요청에 대해 JWT 토큰을 확인하고 유효한 경우 인증 정보를 설정
 * Spring Security 필터 체인에서 UsernamePasswordAuthenticationFilter 이전에 실행
 */
@RequiredArgsConstructor
/**
 * OncePerRequestFilter에서 한번만 실행된다는 것의 의미:
 * TTP 요청이 서블릿 컨테이너(Tomcat 등)를 거쳐 Spring 애플리케이션에 도달할 때,
 * 해당 요청이 여러 서블릿이나 포워드/인클루드 처리를 거치더라도 필터 로직이 중복 실행되지 않고 한 번만 실행되도록 보장.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT 토큰 제공자
    private final JwtTokenProvider tokenProvider;
    
    // 경로 매칭을 위한 패턴 매처
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 필터를 적용하지 않을 경로 목록
    private final List<String> excludedPaths = Arrays.asList(
        "/", "/login", "/signup", "/h2-console/**", 
        "/css/**", "/js/**", "/images/**", "/webjars/**"
    );

    /**
     * 특정 요청 경로에 대해 이 필터를 적용하지 않을지 결정
     * 
     * @param request 현재 요청
     * @return true이면 필터를 건너뛰고, false이면 필터를 적용
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI(); // 현재 HTTP 요청의 URI를 문자열로 반환: http://localhost:8080/api/users라면, path는 /api/users. -> 쿼리 파라미터는 포함하지 않는다.
        
        // 제외 경로 목록과 비교하여 필터 적용 여부 결정
        return excludedPaths.stream()
                .anyMatch(p -> pathMatcher.match(p, path)); // 요청된 현재 HTTP 요청의 URI 경로가 excludedPaths에 포함된 경로와 일치하는지 확인
    }

    /**
     * shouldNotFilter 메서드에서 제외된 경로를 제외한 모든 HTTP 요청에 대해 실행되는 필터 메서드
     * 요청에서 JWT 토큰을 추출하고 유효성을 검사한 후 인증 정보 설정
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // HTTP 요청 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 토큰이 존재하고 유효한 경우 인증 정보 설정
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 토큰에서 인증 정보 추출
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            /**SecurityContext에 인증 정보 저장
             * SecurityContextHolder는 현재 스레드에 대한 보안 컨텍스트를 저장하는 클래스
             * 이후 요청 처리(컨트롤러, 서비스)에서 Spring Security가 이 인증 정보를 사용.
             * 예: @PreAuthorize("hasRole('USER')")는 Authentication의 권한을 확인.
             *
             * if문을 만족하지 못하면 SecurityContextHolder.getContext().getAuthentication()은 null을 반환하게 되고,
             * 권한에 대한 검증이 필요할 때 null을 반환하게 된다. -> 이 경우 인증되지 않은 사용자로 간주됨.
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터 실행: Spring Security 필터 체인에서 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * Authorization 헤더에서 Bearer 토큰을 찾아 반환
     * 
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰, 없으면 null
     */
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        
        // "Bearer " 접두사가 있는 경우 토큰 부분만 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
} 