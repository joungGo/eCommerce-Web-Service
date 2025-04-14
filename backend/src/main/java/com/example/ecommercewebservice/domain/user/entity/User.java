package com.example.ecommercewebservice.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.ecommercewebservice.domain.cart.entity.CartItem;
import com.example.ecommercewebservice.domain.order.entity.Order;
import com.example.ecommercewebservice.domain.review.entity.Review;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    private String phoneNumber;

    private String address;

    private String profileImage;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // 사용자 권한 목록
    @ElementCollection(fetch = FetchType.EAGER) // @ElementCollection은 User 엔터티의 roles 리스트를 별도 테이블에 저장하여 사용자의 권한을 간단히 관리하고, Spring Security의 getAuthorities()로 변환해 인증/인가에 활용한다.
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))// SimpleGrantedAuthority는 Spring Security에서 권한을 표현하는 클래스입니다.
                .collect(Collectors.toList());
    }

    /**
     * 사용자 식별자를 반환
     * 이메일을 사용자 식별자로 사용
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * 계정 만료 여부 확인
     * @return 계정이 만료되지 않았으면 true, 만료되었으면 false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 확인
     * @return 계정이 잠기지 않았으면 true, 잠겼으면 false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부 확인
     * @return 자격 증명이 만료되지 않았으면 true, 만료되었으면 false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부 확인
     * @return 계정이 활성화되어 있으면 true, 비활성화되어 있으면 false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
} 