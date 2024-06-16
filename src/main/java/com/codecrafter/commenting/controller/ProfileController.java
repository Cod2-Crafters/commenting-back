package com.codecrafter.commenting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.request.ProfileRequest;
import com.codecrafter.commenting.domain.response.ProfileResponse;
import com.codecrafter.commenting.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "프로필 조회",
        description = """
                    ★프로필 조회</br>
                    {host}/api/profile/profile/{}
                    """)
    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long id) {
//                                                , @RequestHeader("Authorization") String token) {
        ProfileResponse profileResponse = profileService.getProfile(id);

        log.info("getProfile email: {}", profileResponse.email());
        log.info("getProfile nickname: {}", profileResponse.nickname());
//        log.info("getProfile token: {}", token);

        return ResponseEntity.ok(ApiResponse.success(profileResponse));
    }

    @Operation(summary = "프로필 수정",
        description = """
                    ★프로필 수정</br>
                    {host}/api/profile/profile/{}
                    """)
    @PutMapping("/profile/{id}")
    public ResponseEntity<ApiResponse> updateProfile(@PathVariable Long id
                                                    , @RequestBody ProfileRequest request
                                                    , @RequestHeader("Authorization") String token) {
        log.info("updateProfile id: {}", id);
        log.info("updateProfile request: {}", request);
        log.info("updateProfile token: {}", token);

        ProfileResponse profileResponse = profileService.updateProfile(id, request, token);

        return ResponseEntity.ok(ApiResponse.success(profileResponse));
    }

}
