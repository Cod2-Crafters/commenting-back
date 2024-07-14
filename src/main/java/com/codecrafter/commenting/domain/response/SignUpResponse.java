package com.codecrafter.commenting.domain.response;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpResponse (

    @Schema(description = "식별자", example = "1")
    Long id,
    @Schema(description = "이메일", example = "kin8887@naver.com")
    String email

){
    public static SignUpResponse from(MemberAuth member) {
        return new SignUpResponse(
            member.getId(),
            member.getEmail()
        );
    }
}
