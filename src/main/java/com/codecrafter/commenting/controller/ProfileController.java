package com.codecrafter.commenting.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.request.ProfileRequest;
import com.codecrafter.commenting.domain.response.ProfileResponse;
import com.codecrafter.commenting.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "프로필 수정")
    @PutMapping(value = "/profile/{id}")
    public ResponseEntity<ApiResponse> updateProfile(    @PathVariable Long id
                                                        , @RequestBody ProfileRequest request
                                                        , @RequestHeader("Authorization") String token) {
        ProfileResponse profileResponse = profileService.updateProfile(id, request, token);
        return ResponseEntity.ok(ApiResponse.success(profileResponse));
    }


    @PostMapping(path = "/profile/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필썸네일 파일 업로드")
    public ResponseEntity<ApiResponse>  avatarUpload( @PathVariable Long id
                                                    , @ModelAttribute @Valid MultipartFile avatar
                                                    , @RequestHeader("Authorization") String token) {
        String avatarUrl = profileService.uploadAvatarFile(id, avatar, token);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    @Operation(summary = "썸네일 조회",
        description = """
                    ★프로필 아바타 이미지 조회</br>
                    {host}/api/profile/profile/test.jpg
                    """)
    @GetMapping("/{filename:.+}")
    public ResponseEntity<String> serveFile(@PathVariable String filename) {
        return profileService.serveFile(filename);
    }

}
