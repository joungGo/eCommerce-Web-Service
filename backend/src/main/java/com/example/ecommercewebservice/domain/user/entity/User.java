package com.example.ecommercewebservice.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.ecommercewebservice.domain.cart.entity.CartItem;
import com.example.ecommercewebservice.domain.order.entity.Order;
import com.example.ecommercewebservice.domain.review.entity.Review;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
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

    /**
     * 배송지 목록
     * CascadeType.ALL: 부모 엔티티의 상태 변경을 자식 엔티티에 전파
     * orphanRemoval = true: 부모와의 관계가 끊어진 자식 엔티티를 자동으로 삭제
     * [CascadeType vs orphanRemoval]
     * 1. 부모 엔티티의 상태 변경이 자식에게 전파되고
     * 2. 관계가 끊어진 자식 엔티티가 자동으로 삭제되어
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval = true: 자식 엔티티가 부모 엔티티와의 관계를 끊으면 자동으로 삭제
    @Builder.Default // 이것이 없다면 @Builder가 생성하는 빌더는 이 초기값을 무시하기 때문에 null이 된다.
    private List<Address> addresses = new ArrayList<>(); // 빌더 패턴을 사용할 때 addresses 필드가 명시적으로 설정되지 않으면 new ArrayList<>()를 기본값으로 사용

//    private String profileImage;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // 사용자 권한 목록
    @ElementCollection(fetch = FetchType.EAGER) // @ElementCollection은 User 엔터티의 roles 리스트를 별도 테이블에 저장하여 사용자의 권한을 간단히 관리하고, Spring Security의 getAuthorities()로 변환해 인증/인가에 활용한다.
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * 사용자의 실제 이름을 반환
     * @return 사용자의 실제 이름
     */
    public String getActualUsername() {
        return this.username;
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