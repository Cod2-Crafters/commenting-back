package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.config.jwt.JwtUtil;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberAuthController {

    private final JwtUtil jwtUtil;

    @Operation(summary = "google oauth 인증",
        description = """
					★구글 인증이후 REDIRECT URL</br>
					접근시 아래 경로 호출 요망</br>
			        {host}/oauth/google
			        """)
    @GetMapping("/oauth")
    public String oauthLoginInfo(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        String email = (String) attributes.get("email");

        System.out.println("User email: " + email);
        return email;
    }

}