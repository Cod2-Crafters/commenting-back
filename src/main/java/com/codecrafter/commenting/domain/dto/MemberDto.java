package com.codecrafter.commenting.domain.dto;

import com.codecrafter.commenting.domain.entity.base.Provider;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import lombok.*;

@Getter
@Setter
public class MemberDto {
    private String email;
    private Provider provider;

    public MemberAuth toMember() {
        return MemberAuth.builder()
            .email(email)
            .provider(provider)
            .build();
    }

}