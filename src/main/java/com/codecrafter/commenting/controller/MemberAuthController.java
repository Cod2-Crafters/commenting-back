package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.config.jwt.TokenProvider;
import com.codecrafter.commenting.domain.enumeration.Provider;
import com.codecrafter.commenting.domain.request.SignInRequest;
import com.codecrafter.commenting.domain.response.SignInResponse;
import com.codecrafter.commenting.service.MemberAuthService;
import com.codecrafter.commenting.service.MemberService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberAuthController {
    private final MemberAuthService memberAuthService;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "google oauth 인증",
        description = """
					★구글 인증이후 REDIRECT URL</br>
					접근시 아래 경로 호출 요망</br>
			        {host}/oauth/google
			        """)
    @GetMapping("/oauth")
    public String oauthLoginInfo(@AuthenticationPrincipal OAuth2User principal, Authentication authentication) {
        Map<String, Object> attributes = principal.getAttributes();

        String email = (String) attributes.get("email");
        HttpHeaders headers = new HttpHeaders();

        SignInRequest request = new SignInRequest(email, Provider.GOOGLE, null);
        SignInResponse signInResponse = memberService.signIn(request);

        String rid = signInResponse.id().toString();
        String remail = signInResponse.email();
        String rtoken = signInResponse.token();
        String ravatarPath = signInResponse.avatarPath();

        headers.add("Authorization", "Bearer " + signInResponse.token());

        return "redirect:/frontRedirect.html?success=true"  + "&id="            + rid
                                                            + "&email="         + remail
                                                            + "&token="         + rtoken
                                                            + "&avatarPath="    + ravatarPath;
    }


}