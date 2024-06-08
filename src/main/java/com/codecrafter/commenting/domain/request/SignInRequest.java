package com.codecrafter.commenting.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInRequest(
        @Schema(description = "이메일", example = "jayce@crafter.com")
        String email,
        @Schema(description = "비번", example = "0000")
        String password
) {
}
