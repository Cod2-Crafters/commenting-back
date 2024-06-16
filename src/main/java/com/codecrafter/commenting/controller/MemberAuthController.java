package com.codecrafter.commenting.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberAuthController {


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

        return email;
    }

}