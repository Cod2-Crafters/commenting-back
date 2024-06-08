package com.codecrafter.commenting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.codecrafter.commenting.config.filter.JwtAuthenticationFilter;
import com.codecrafter.commenting.service.MemberAuthService;

import lombok.RequiredArgsConstructor;

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
				.requestMatchers("/", "/swagger-ui/**", "/v3/**", "/api/member/sign-in", "/api/member/sign-up",
					"/api/auth/validate")
				.permitAll()
				.anyRequest()
				.permitAll())
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
