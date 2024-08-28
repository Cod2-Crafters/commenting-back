package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.request.SignInRequest;
import com.codecrafter.commenting.domain.request.SignUpRequest;
import com.codecrafter.commenting.domain.response.SettingResponse;
import com.codecrafter.commenting.domain.response.SignInResponse;
import com.codecrafter.commenting.domain.response.SignUpResponse;
import com.codecrafter.commenting.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ApiResponse> signUp(@RequestBody SignUpRequest request) {
        SignUpResponse signUpResponse = memberService.registMember(request);
        return new ResponseEntity<>(ApiResponse.success(signUpResponse), HttpStatus.CREATED);
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

    @Operation(summary = "로그아웃",
        description = """
                        ★로그아웃</br>
                        {host}/api/member/sign-out
                        """)
    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse> signOut(@RequestHeader("Authorization") String token) {
        memberService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 성공적으로 완료되었습니다."));
    }

    @Operation(summary = "탈퇴",
        description = """
                        ★회원탈퇴</br>
                        {host}/api/member/unregister
                        """)
    @PostMapping("/unregister")
    public ResponseEntity<Void> unregister(@RequestHeader("Authorization") String token,
                                             @Parameter(description = "이메일", example = "jayce@crafter.com") @RequestParam String email) {
        memberService.unregister(email, token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 중복 검사",
        description = """
                        ★이메일 중복 검사</br>
                        {host}/api/member/check-email
                        """)
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmailDuplicate(@Parameter(description = "이메일", example = "jayce@crafter.com") @RequestParam String email) {
        try {
            boolean isDup = memberService.chkDupEmail(email);
            return ResponseEntity.ok(ApiResponse.success(isDup));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage(), e));
        }
    }

//    @GetMapping("/jwt-test")
//    public String getUser(@AuthenticationPrincipal MemberAuth memberAuth) {
//        log.info("getUser memberAuth : {}", memberAuth.getEmail());
//        log.info("getUser memberAuth : {}", memberAuth.getId());
//        return memberAuth.getEmail();
//    }

    @Operation(summary = "광역 질문 승인 설정",
        description = """
                            ★광역 질문 승인 설정</br>
                            토큰 필수</br>
                            {host}/api/member/allow-global-question
                            """)
    @PutMapping("/allow-global-question")
    public ResponseEntity<ApiResponse> setAllowGlobalQuestion() {
        SettingResponse settingResponse = memberService.setAllowGlobalQuestion();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "이메일 알림 수신 설정",
        description = """
                        ★이메일 알림 수신 설정</br>
                        토큰 필수</br>
                        {host}/api/member/email-notification
                        """)
    @PutMapping("/email-notification")
    public ResponseEntity<ApiResponse> setEmailNotification() {
        SettingResponse settingResponse = memberService.setEmailNotification();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "스페이스 일시 중지 설정",
        description = """
                        ★스페이스 일시 중지 설정</br>
                        토큰 필수</br>
                        {host}/api/member/space-pause
                        """)
    @PutMapping("/space-pause")
    public ResponseEntity<ApiResponse> setSpacePause() {
        SettingResponse settingResponse = memberService.setSpacePause();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "비로그인 질문 제한 설정",
        description = """
                        ★비로그인 질문 제한 설정</br>
                        토큰 필수</br>
                        {host}/api/member/allow-anonymous
                        """)
    @PutMapping("/allow-anonymous")
    public ResponseEntity<ApiResponse> setAllowAnonymous() {
        SettingResponse settingResponse = memberService.setAllowAnonymous();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }
}
