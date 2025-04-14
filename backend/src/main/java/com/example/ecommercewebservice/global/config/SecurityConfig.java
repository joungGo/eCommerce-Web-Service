package com.example.ecommercewebservice.global.config;

import com.example.ecommercewebservice.global.exception.security.CustomAccessDeniedHandler;
import com.example.ecommercewebservice.global.exception.security.CustomAuthenticationEntryPoint;
import com.example.ecommercewebservice.global.filter.JwtAuthenticationFilter;
import com.example.ecommercewebservice.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * 애플리케이션의 보안 설정을 정의하고 JWT 인증 필터를 등록
 * 
 * 인증(Authentication)과 인가(Authorization) 관련 설정을 포함:
 * - 인증: 사용자가 누구인지 확인 (로그인 과정)
 * - 인가: 인증된 사용자가 특정 리소스에 접근할 권한이 있는지 확인
 * 
 * [변경사항]
 * URL 패턴 기반 권한 제어에서 어노테이션 기반 권한 제어로 변경
 * RequireRole 커스텀 어노테이션을 통해 메서드 레벨에서 권한 검사 수행
 */
@Configuration
@EnableWebSecurity // Spring Security의 웹 보안 기능 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰 제공자
    private final JwtTokenProvider jwtTokenProvider;
    
    // 인가 예외(권한 없음) 처리기
    private final CustomAccessDeniedHandler accessDeniedHandler;
    
    // 인증 예외(인증되지 않음) 처리기
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 비밀번호 인코더 빈 등록
     * 사용자 비밀번호를 안전하게 해시화하는 데 사용
     * 
     * @return BCrypt 알고리즘을 사용하는 PasswordEncoder 구현체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 빈 등록
     * 사용자 인증 처리를 담당
     * 
     * @param authenticationConfiguration 인증 설정
     * @return AuthenticationManager 인증 관리자
     * @throws Exception 인증 관리자 생성 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 보안 필터 체인 설정
     * HTTP 요청에 대한 보안 규칙 및 JWT 인증 필터 설정
     * 
     * [변경사항]
     * URL 패턴 기반 권한 설정 대신 기본 인증 설정만 유지
     * 상세 권한 제어는 메서드 레벨 어노테이션으로 이동
     *
     * @param http HttpSecurity 설정 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 기능 비활성화
            .csrf(csrf -> csrf.disable())
            
            // HTTP 기본 인증 비활성화
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // 세션 관리 정책 설정 (STATELESS: 서버가 세션을 생성하지 않음)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 예외 처리 설정
            .exceptionHandling(exception -> exception
                .accessDeniedHandler(accessDeniedHandler)           // 인가 실패 처리기
                .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 처리기
            )
            
            // URL별 접근 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // 공개 리소스 - 인증 없이 접근 가능
                .requestMatchers("/h2-console/**", "/api/auth/**", "/api/resources/public").permitAll()
                .requestMatchers("/", "/login", "/signup", "/access-denied").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                /* [URL 패턴 기반 권한 설정 제거]
                // 기존 URL 패턴 기반 권한 설정
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 관리자 전용 API
                .requestMatchers("/admin/**").hasRole("ADMIN")      // 관리자 전용 페이지
                .requestMatchers("/api/user/**").hasRole("USER")    // 일반 사용자 API
                */
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                             UsernamePasswordAuthenticationFilter.class);

        // H2 콘솔 사용을 위한 설정 (프레임 사용 허용)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * WebSecurityCustomizer 설정
     * 웹에서 정적 파일에 대한 보안 필터를 비활성화하여 성능을 향상시킴
     *
     * @return WebSecurityCustomizer 설정 객체
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
    }
} 