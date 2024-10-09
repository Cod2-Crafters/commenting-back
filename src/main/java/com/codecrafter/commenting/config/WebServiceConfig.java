package com.codecrafter.commenting.config;

import com.codecrafter.commenting.controller.interceptor.MemberSettingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebServiceConfig implements WebMvcConfigurer {

    private final MemberSettingInterceptor memberSettingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSettingInterceptor)
                .addPathPatterns("/api/member/setting/**");
    }

}
