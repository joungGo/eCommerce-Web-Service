package com.example.ecommercewebservice.global.security.aspect;

import com.example.ecommercewebservice.config.UserRole;
import com.example.ecommercewebservice.global.security.annotation.RoleRequired;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * RoleRequired 어노테이션을 처리하는 AOP 구현체
 * 어노테이션이 적용된 메서드가 호출될 때 권한 검사를 수행
 */
@Slf4j
@Aspect
@Component
public class RoleRequiredAspect {

    /**
     * RoleRequired 어노테이션이 적용된 메서드 호출을 가로채서 권한 검사
     * 
     * @param joinPoint 가로챈 메서드 실행 지점
     * @param roleRequired 메서드에 적용된 RoleRequired 어노테이션
     * @return 원본 메서드의 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외 또는 권한 부족 시 AccessDeniedException
     */
    @Around("@annotation(roleRequired) || @within(roleRequired)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RoleRequired roleRequired) throws Throwable {
        // 메서드 레벨 어노테이션이 없으면 클래스 레벨 어노테이션 확인
        if (roleRequired == null) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            
            // 메서드에 어노테이션이 있는지 확인
            roleRequired = method.getAnnotation(RoleRequired.class);
            
            // 메서드에 없으면 클래스에서 확인
            if (roleRequired == null) {
                roleRequired = method.getDeclaringClass().getAnnotation(RoleRequired.class);
            }
        }
        
        // 어노테이션이 없으면 그냥 진행
        if (roleRequired == null) {
            return joinPoint.proceed();
        }
        
        // 현재 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 사용자의 보호된 메서드 접근 시도: {}", joinPoint.getSignature());
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }
        
        UserRole[] requiredRoles = roleRequired.value();
        log.debug("권한 검사 시작: 필요한 권한={}, 메서드={}", 
                 Arrays.toString(requiredRoles), joinPoint.getSignature());
        
        // 필요한 권한 중 하나라도 있는지 확인
        boolean hasRequiredRole = Arrays.stream(requiredRoles)
            .anyMatch(role -> authentication.getAuthorities().contains(
                new SimpleGrantedAuthority(role.getRole())));
        
        if (!hasRequiredRole) {
            log.warn("권한 부족: 사용자={}, 필요한 권한={}, 메서드={}", 
                    authentication.getName(), Arrays.toString(requiredRoles), joinPoint.getSignature());
            throw new AccessDeniedException("접근 권한이 없습니다. 필요한 권한: " + Arrays.toString(requiredRoles));
        }
        
        log.debug("권한 검사 통과: 사용자={}, 메서드={}", 
                authentication.getName(), joinPoint.getSignature());
        
        // 권한 검사 통과 시 원래 메서드 실행
        return joinPoint.proceed();
    }
} 