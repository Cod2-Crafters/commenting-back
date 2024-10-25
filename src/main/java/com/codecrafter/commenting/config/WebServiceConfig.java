package com.codecrafter.commenting.config;

import com.codecrafter.commenting.config.filter.ContentCachingFilter;
import com.codecrafter.commenting.controller.interceptor.MemberSettingInterceptor;
import com.codecrafter.commenting.controller.interceptor.RecommendInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebServiceConfig implements WebMvcConfigurer {

    private final MemberSettingInterceptor memberSettingInterceptor;
    private final RecommendInterceptor recommendInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSettingInterceptor)
                .addPathPatterns("/api/member/setting/**");

        registry.addInterceptor(recommendInterceptor)
            .addPathPatterns("/api/recommends/**");
    }

    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
        FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentCachingFilter());
        registrationBean.addUrlPatterns("/api/recommends/thanked", "/api/recommends/likes");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }

}
