package com.codecrafter.commenting.domain.request;

import com.codecrafter.commenting.domain.entity.base.Provider;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignInRequest(
    @Schema(description = "이메일", example = "kin8887@naver.com")
    String email,
    @Schema(description = "공급자", example = "BASE")
    Provider provider,
    @Schema(description = "비번", example = "0000")
    String password
) {
}
