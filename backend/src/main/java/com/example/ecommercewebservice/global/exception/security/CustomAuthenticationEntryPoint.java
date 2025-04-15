package com.example.ecommercewebservice.global.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증(Authentication) 실패 처리를 위한 진입점
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됨
 * (401 Unauthorized 에러 발생 시)
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Jackson 라이브러리의 핵심 클래스입니다.
     * JSON 데이터를 Java 객체로 변환(역직렬화, deserialization)하거나, Java 객체를 JSON 데이터로 변환(직렬화, serialization)하는 데 사용
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 실행되는 핸들링 메서드
     * API 요청의 경우 JSON 응답 반환, 페이지 요청의 경우 로그인 페이지로 리다이렉트
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 발생한 인증 예외
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        // AJAX 요청 또는 API 요청인 경우 JSON 응답 반환
        if (isAjaxRequest(request) || isApiRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorDetails.put("error", "Unauthorized");
            errorDetails.put("message", "인증이 필요합니다. 로그인 후 다시 시도해주세요.");
            errorDetails.put("path", request.getRequestURI());
            
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        } else {
            // 일반 페이지 요청인 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/login?error=true");
        }
    }
    
    /**
     * AJAX 요청인지 확인하는 메서드
     * 
     * @param request HTTP 요청
     * @return AJAX 요청 여부
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    
    /**
     * API 요청인지 확인하는 메서드
     * 경로가 /api로 시작하면 API 요청으로 판단
     * 
     * @param request HTTP 요청
     * @return API 요청 여부
     */
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }
} 