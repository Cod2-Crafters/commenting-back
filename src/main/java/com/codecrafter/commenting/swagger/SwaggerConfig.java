package com.codecrafter.commenting.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_NAME = "authorization";

    @Bean
    public OpenAPI swaggerApi() {
        //스웨거 JWT 설정
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
    @Bean
    public GroupedOpenApi customOpenApi() {
        // 아래 경로 스캔하여 스웨거 범위 지정
        return GroupedOpenApi.builder()
                            .group("commenting")
                            .packagesToScan("com.codecrafter.commenting")
                            .build();
    }
}