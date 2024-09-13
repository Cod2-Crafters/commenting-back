package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.response.SettingResponse;
import com.codecrafter.commenting.service.MemberSettingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberSettingController {

    private final MemberSettingService memberSettingService;

    @Operation(summary = "광역 질문 승인 설정 ★",
        description = """
                            ★광역 질문 승인 설정</br>
                            {host}/api/member/allow-global-question
                            """)
    @PutMapping("/allow-global-question")
    public ResponseEntity<ApiResponse> setAllowGlobalQuestion() {
        SettingResponse settingResponse = memberSettingService.setAllowGlobalQuestion();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "이메일 알림 수신 설정 ★",
        description = """
                        ★이메일 알림 수신 설정</br>
                        {host}/api/member/email-notification
                        """)
    @PutMapping("/email-notification")
    public ResponseEntity<ApiResponse> setEmailNotification() {
        SettingResponse settingResponse = memberSettingService.setEmailNotification();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "스페이스 일시 중지 설정 ★",
        description = """
                        ★스페이스 일시 중지 설정</br>
                        {host}/api/member/space-pause
                        """)
    @PutMapping("/space-pause")
    public ResponseEntity<ApiResponse> setSpacePause() {
        SettingResponse settingResponse = memberSettingService.setSpacePause();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "비로그인 질문 제한 설정 ★",
        description = """
                        ★비로그인 질문 제한 설정</br>
                        {host}/api/member/allow-anonymous
                        """)
    @PutMapping("/allow-anonymous")
    public ResponseEntity<ApiResponse> setAllowAnonymous() {
        SettingResponse settingResponse = memberSettingService.setAllowAnonymous();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }
}
