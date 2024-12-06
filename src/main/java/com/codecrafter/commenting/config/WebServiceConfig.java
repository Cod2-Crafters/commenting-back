package com.codecrafter.commenting.config;

import com.codecrafter.commenting.config.filter.ContentCachingFilter;
import com.codecrafter.commenting.controller.interceptor.ConversationQuestionInterceptor;
import com.codecrafter.commenting.controller.interceptor.LoginCheckInterceptor;
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

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final RecommendInterceptor recommendInterceptor;
    private final ConversationQuestionInterceptor conversationQuestionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
            .addPathPatterns(
                "/api/member/setting/**", // 설정 수정
                "/api/recommends/**",
                "/api/conversations/question/update", // 질문수정
                "/api/conversations/question/*", // 질문 삭제
                "/api/conversations/answer", // 답변 작성
                "/api/conversations/answer/update", // 답변 수정
                "/api/conversations/answer/*", // 답변 삭제
                "/api/conversations/members/*/send", // 보낸질문 조회
                "/api/statistics" // 통계 조회
            )

        ;

        registry.addInterceptor(recommendInterceptor)
            .addPathPatterns("/api/recommends/**");

        registry.addInterceptor(conversationQuestionInterceptor)
            .addPathPatterns("/api/conversations/question");
    }

    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
        FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentCachingFilter());
        registrationBean.addUrlPatterns("/api/recommends/thanked", "/api/recommends/likes",
            "/api/conversations/question");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }

}
