package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.request.SignInRequest;
import com.codecrafter.commenting.domain.request.SignUpRequest;
import com.codecrafter.commenting.domain.response.SignInResponse;
import com.codecrafter.commenting.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입",
            description = """
                        ★BASE 회원가입</br>
                        {host}/api/member/sign-up
                        """)
    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody SignUpRequest request) {
        return ApiResponse.success(memberService.registMember(request));
    }

    @Operation(summary = "로그인",
            description = """
                        ★로그인</br>
                        {host}/api/member/sign-in
                        """)
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInRequest request) {
        SignInResponse signInResponse = memberService.signIn(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + signInResponse.token());

        return ResponseEntity.ok().headers(headers).body(ApiResponse.success(signInResponse));
    }

//    @Operation(summary = "로그아웃",
//        description = """
//					★로그아웃</br>
//			        {host}/api/member/sign-out
//			        """)
//    @PostMapping("/sign-out")
//    public ResponseEntity signOut(@RequestBody SignInRequest request) {
//        return null;
//    }

    @GetMapping("/jwt-test")
    public String getUser(@AuthenticationPrincipal MemberAuth memberAuth) {
        log.info("memberAuth : {}", memberAuth.getEmail());
        log.info("memberAuth : {}", memberAuth.getId());
        return memberAuth.getEmail();
    }

}
