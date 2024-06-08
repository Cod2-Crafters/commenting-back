package com.codecrafter.commenting.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInResponse(
        @Schema(description = "이메일", example = "jayce@crafter.com")
        String email,
        String token
) {
}
