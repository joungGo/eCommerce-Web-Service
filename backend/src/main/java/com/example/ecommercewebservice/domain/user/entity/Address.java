package com.example.ecommercewebservice.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId; // 배송지 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @Column(nullable = false)
    private String recipient; // 수령인 이름

    @Column(nullable = false)
    private String postalCode; // 우편번호

    @Column(nullable = false)
    private String address; // 주소

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Column(nullable = false)
    private boolean isDefault; // 기본 배송지 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일시

    public void update(String recipient, String postalCode, String address, String phone, boolean isDefault) {
        this.recipient = recipient;
        this.postalCode = postalCode;
        this.address = address;
        this.phoneNumber = phone;
        this.isDefault = isDefault;
    }
}