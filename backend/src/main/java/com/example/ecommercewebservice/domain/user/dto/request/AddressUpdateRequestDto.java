package com.example.ecommercewebservice.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 주소 수정 요청을 위한 DTO
 * 
 * 사용 위치:
 * - UserUpdateRequestDto.addresses - 사용자 프로필 수정 시 주소 정보를 담는 객체
 * - UserService.updateAddresses() - 주소 정보 업데이트 로직에서 사용
 * 
 * 용도:
 * - 사용자의 주소 정보를 추가, 수정, 삭제하기 위한 요청 데이터를 담는 객체
 * - addressId가 null이면 새 주소 추가, 있으면 기존 주소 수정
 * - 기본 주소는 반드시 하나만 존재해야 함
 */
@Getter
@NoArgsConstructor
public class AddressUpdateRequestDto {
    private Long addressId;  // null이면 새 주소 추가, 있으면 수정 또는 삭제

    @NotBlank(message = "수취인 이름은 필수입니다.")
    private String recipient;

    @NotBlank(message = "우편번호는 필수입니다.")
    @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
    private String postalCode;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", 
             message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String phone;

    private boolean isDefault;
} 