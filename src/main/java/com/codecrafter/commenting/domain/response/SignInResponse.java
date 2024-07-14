package com.codecrafter.commenting.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInResponse(
        @Schema(description = "식별자", example = "1")
        Long id,
        @Schema(description = "이메일", example = "kin8887@naver.com")
        String email,
        @Schema(description = "토큰값")
        String token
) {
}
