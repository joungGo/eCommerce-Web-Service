package com.example.ecommercewebservice.domain.user.dto.response;

import com.example.ecommercewebservice.domain.user.entity.Address;
import com.example.ecommercewebservice.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 프로필 응답을 위한 DTO
 * 
 * 사용 위치:
 * - UserController.updateProfile() - PUT /api/users/me 엔드포인트의 응답으로 사용
 * - UserService.updateProfile() - 프로필 수정 후 응답 데이터를 생성할 때 사용
 * 
 * 용도:
 * - 사용자의 프로필 정보를 클라이언트에 반환하기 위한 객체
 * - 무한 순환 참조를 방지하기 위해 AddressResponseDto를 내부 클래스로 정의
 * - from() 메서드를 통해 User 엔티티를 DTO로 변환
 */
@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String email;
    private String username;
    private String phoneNumber;
    private List<AddressResponseDto> addresses;
    private List<String> roles;
    private LocalDateTime updatedAt;

    /**
     * 주소 정보 응답을 위한 내부 DTO
     * 
     * 사용 위치:
     * - UserResponseDto.addresses - 사용자 프로필 응답의 주소 정보를 담는 객체
     * 
     * 용도:
     * - 주소 정보만을 포함하는 간단한 DTO
     * - User 엔티티와의 무한 순환 참조를 방지
     */
    @Getter
    @Builder
    public static class AddressResponseDto {
        private Long addressId;
        private String recipient;
        private String postalCode;
        private String address;
        private String phoneNumber;
        private boolean isDefault;
        private LocalDateTime createdAt;

        public static AddressResponseDto from(Address address) {
            return AddressResponseDto.builder()
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

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .addresses(user.getAddresses().stream()
                        .map(AddressResponseDto::from)
                        .collect(Collectors.toList()))
                .roles(user.getRoles())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 