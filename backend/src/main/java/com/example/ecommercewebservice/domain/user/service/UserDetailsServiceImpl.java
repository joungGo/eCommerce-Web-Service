package com.example.ecommercewebservice.domain.user.service;

import com.example.ecommercewebservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService 구현 클래스
 * 사용자 인증을 위한 사용자 정보를 데이터베이스에서 조회하는 서비스
 *
 * 실제 인증을 처리할 때 호출되는 메서드!!
 * /login 경로로 요청시 이 클래스의 loadUserByUsername 메서드가 호출됨 -> DB에서 사용자 정보 조회 -> 로그인한 사용자의 존재 여부 확인 후 UserDetails 객체로 반환.
 * UserDetails 객체에는 사용자 정보(이메일, 비밀번호 등)와 권한 정보가 포함됨. -> 비밀번호는 DB에서 조회시 로드됨.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // 사용자 정보 저장소
    private final UserRepository userRepository;

    /**
     * 사용자 이름(이메일)으로 UserDetails 객체 로드
     * Spring Security에서 인증 시 사용자 정보를 조회하는 데 사용
     * 
     * @param username 조회할 사용자의 이메일
     * @return UserDetails 조회된 사용자 정보
     * @throws UsernameNotFoundException 사용자가 존재하지 않을 경우 발생하는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsService - 사용자 정보 로드 시도: username={}", username);
        
        try {
            // 사용자 이메일로 사용자 정보 조회
            UserDetails userDetails = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("이메일이 " + username + "인 사용자를 찾을 수 없습니다."));
            
            log.info("UserDetailsService - 사용자 정보 로드 성공: username={}, 권한={}", 
                    userDetails.getUsername(), userDetails.getAuthorities());
            return userDetails;
        } catch (Exception e) {
            log.error("UserDetailsService - 사용자 정보 로드 중 오류 발생: ", e);
            throw e;
        }
    }
} 