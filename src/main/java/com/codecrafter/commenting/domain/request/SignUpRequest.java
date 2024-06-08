package com.codecrafter.commenting.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpRequest (
    @Schema(description = "이메일", example = "jayce@crafter.com")
    String email,
    @Schema(description = "제공자", example = "KAKAO")
    String provider,

    @Schema(description = "비번", example = "0000")
    String password
) {
}
