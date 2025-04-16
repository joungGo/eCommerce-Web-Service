package com.example.ecommercewebservice.domain.user.dto.adress;

import com.example.ecommercewebservice.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long addressId;
    private String recipient;
    private String postalCode;
    private String address;
    private String phoneNumber;
    private boolean isDefault;
    private LocalDateTime createdAt;

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