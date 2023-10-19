package com.example.project02.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsCertification {
    private String phone;
    private String certificationNumber;

    @Builder
    public SmsCertification(String phone, String certificationNumber) {
        this.phone = phone;
        this.certificationNumber = certificationNumber;
    }

}
