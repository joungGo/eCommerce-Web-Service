package com.example.ecommercewebservice.global.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인가(Authorization) 실패 처리를 위한 핸들러
 * 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출됨
 * (403 Forbidden 에러 발생 시)
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 접근 거부 예외가 발생했을 때 실행되는 핸들링 메서드
     * API 요청의 경우 JSON 응답 반환, 페이지 요청의 경우 접근 거부 페이지로 리다이렉트
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param accessDeniedException 발생한 접근 거부 예외
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // AJAX 요청 또는 API 요청인 경우 JSON 응답 반환
        if (isAjaxRequest(request) || isApiRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", HttpServletResponse.SC_FORBIDDEN);
            errorDetails.put("error", "Forbidden");
            errorDetails.put("message", "접근 권한이 없습니다. 필요한 권한: " + getRequiredRole(request));
            errorDetails.put("path", request.getRequestURI());
            
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        } else {
            // 일반 페이지 요청인 경우 접근 거부 페이지로 리다이렉트
            response.sendRedirect("/access-denied");
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
    
    /**
     * 접근하려는 리소스에 필요한 역할을 추정하는 메서드
     * URL 패턴을 기반으로 필요한 권한을 반환
     * 
     * @param request HTTP 요청
     * @return 필요한 역할 문자열
     */
    private String getRequiredRole(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        if (uri.startsWith("/api/admin") || uri.startsWith("/admin")) {
            return "ADMIN";
        } else if (uri.startsWith("/api/user")) {
            return "USER";
        }
        
        return "인증된 사용자";
    }
} 