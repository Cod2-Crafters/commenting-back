package com.codecrafter.commenting.domain.response;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpResponse (
    String email,
    String password
){
    public static SignUpResponse from(MemberAuth member) {
        return new SignUpResponse(
            member.getEmail(),
            member.getPassword()
        );
    }
}
