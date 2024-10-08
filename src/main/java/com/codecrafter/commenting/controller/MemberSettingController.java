package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.response.SettingDataResponse;
import com.codecrafter.commenting.domain.response.SettingResponse;
import com.codecrafter.commenting.service.MemberSettingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/setting")
public class MemberSettingController {

    private final MemberSettingService memberSettingService;

    @Operation(summary = "설정 데이터 조회 ★",
        description = """
                        ★설정 데이터 조회</br>
                        {host}/api/member/setting
                      """)
    @GetMapping
    public ResponseEntity<ApiResponse> getSettingData() {
        SettingDataResponse settingDataResponse = memberSettingService.getSettingData();
        return ResponseEntity.ok(ApiResponse.success(settingDataResponse));
    }

    @Operation(summary = "광역 질문 승인 설정 ★",
        description = """
                            ★광역 질문 승인 설정</br>
                            {host}/api/member/setting/allow-global-question
                            """)
    @PutMapping("/allow-global-question")
    public ResponseEntity<ApiResponse> setAllowGlobalQuestion() {
        SettingResponse settingResponse = memberSettingService.setAllowGlobalQuestion();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "이메일 알림 수신 설정 ★",
        description = """
                        ★이메일 알림 수신 설정</br>
                        {host}/api/member/setting/email-notification
                        """)
    @PutMapping("/email-notification")
    public ResponseEntity<ApiResponse> setEmailNotification() {
        SettingResponse settingResponse = memberSettingService.setEmailNotification();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "스페이스 일시 중지 설정 ★",
        description = """
                        ★스페이스 일시 중지 설정</br>
                        {host}/api/member/setting/space-pause
                        """)
    @PutMapping("/space-pause")
    public ResponseEntity<ApiResponse> setSpacePause() {
        SettingResponse settingResponse = memberSettingService.setSpacePause();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }

    @Operation(summary = "비로그인 질문 제한 설정 ★",
        description = """
                        ★비로그인 질문 제한 설정</br>
                        {host}/api/member/setting/allow-anonymous
                        """)
    @PutMapping("/allow-anonymous")
    public ResponseEntity<ApiResponse> setAllowAnonymous() {
        SettingResponse settingResponse = memberSettingService.setAllowAnonymous();
        return ResponseEntity.ok(ApiResponse.success(settingResponse));
    }
}
