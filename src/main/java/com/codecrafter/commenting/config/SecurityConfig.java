package com.codecrafter.commenting.config;

import com.codecrafter.commenting.config.filter.JwtAuthenticationFilter;
import com.codecrafter.commenting.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberAuthService oAuthService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/auth/oauth", true)
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService)))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/swagger-ui/**", "/v3/**", "/api/member/sign-in", "/api/member/sign-up", "/api/auth/validate")
                .permitAll()
                .requestMatchers("/ouath/google").authenticated()   // google ouath 로그인 화면경로
                .anyRequest().permitAll() // 그 외 모든 요청은 허용
//                .anyRequest() // 모든요청에
//                .authenticated() // 인증이되야한다
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


/*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
    }
*/
}
