package com.codecrafter.commenting.domain.request;

import com.codecrafter.commenting.domain.entity.base.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record SignUpRequest (
    @Schema(description = "이메일", example = "jayce@crafter.com")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    String email,
    @Schema(description = "공급자", example = "BASE")
    @Nullable
    Provider provider,
    @Schema(description = "비밀번호", example = "0000")
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    String password
) {
    public static SignUpRequest of(String email, @Nullable String provider, String password) {
        Provider sProvider = provider == null ? Provider.BASE : Provider.valueOf(provider.toUpperCase());
        return new SignUpRequest(email, sProvider, password);
    }
}
