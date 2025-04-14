package com.example.ecommercewebservice.domain.user.repository;

import com.example.ecommercewebservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 정보에 대한 데이터 액세스 인터페이스
 * Spring Data JPA를 사용하여 사용자 엔티티에 대한 CRUD 작업을 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return Optional<User> 조회된 사용자 (없으면 빈 Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 존재 여부 확인
     * 
     * @param email 확인할 이메일
     * @return boolean 이메일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);
} 