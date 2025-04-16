package com.example.ecommercewebservice.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 프로필 수정 요청을 위한 DTO
 * 
 * 사용 위치:
 * - UserController.updateProfile() - PUT /api/users/me 엔드포인트에서 사용
 * - UserService.updateProfile() - 프로필 수정 비즈니스 로직에서 사용
 * 
 * 용도:
 * - 사용자의 비밀번호, 이름, 전화번호, 주소 정보를 수정하기 위한 요청 데이터를 담는 객체
 * - 모든 필드는 선택적으로 수정 가능
 * - 주소 정보는 추가, 수정, 삭제가 가능
 */
@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    private String username;

    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", 
             message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String phoneNumber;

    private List<AddressUpdateRequestDto> addresses;
} 