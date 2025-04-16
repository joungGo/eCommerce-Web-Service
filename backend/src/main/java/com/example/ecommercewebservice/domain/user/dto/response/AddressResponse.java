package com.example.ecommercewebservice.domain.user.dto.response;

import com.example.ecommercewebservice.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주소 정보 응답 DTO
 * 
 * 사용 위치:
 * - AddressController - 주소 관련 API 응답으로 사용
 * - UserService.getAddresses() - 사용자의 주소 목록 조회 시 사용
 * 
 * 용도:
 * - 사용자의 주소 정보를 클라이언트에 반환하기 위한 객체
 * - 주소 정보만을 독립적으로 반환할 때 사용
 * - UserResponseDto.AddressResponseDto와 유사하지만 독립적인 응답으로 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    /**
     * 주소 식별자
     */
    private Long addressId;
    
    /**
     * 수령인 이름
     */
    private String recipient;
    
    /**
     * 우편번호
     */
    private String postalCode;
    
    /**
     * 상세 주소
     */
    private String address;
    
    /**
     * 배송지 연락처
     */
    private String phoneNumber;
    
    /**
     * 기본 배송지 여부
     */
    private boolean isDefault;
    
    /**
     * 주소 등록 일시
     */
    private LocalDateTime createdAt;

    /**
     * Address 엔티티를 AddressResponse DTO로 변환
     * 
     * @param address 변환할 Address 엔티티
     * @return 생성된 AddressResponse 객체
     */
    public static AddressResponse from(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .recipient(address.getRecipient())
                .postalCode(address.getPostalCode())
                .address(address.getAddress())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}