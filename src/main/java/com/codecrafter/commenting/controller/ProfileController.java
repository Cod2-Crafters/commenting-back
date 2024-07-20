package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.MemberInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.request.ProfileRequest;
import com.codecrafter.commenting.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
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
                        {host}/api/profile/profile/{id}
                        """)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long id) {
        MemberInfoDto profileResponse = profileService.getProfileResponse(id);
        return ResponseEntity.ok(ApiResponse.success(profileResponse));
    }

    @Operation(summary = "프로필 수정",
        description = """
                        ★프로필 수정</br>
                        {host}/api/profile
                        """)
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> updateProfile(    @PathVariable Long id
                                                        , @RequestBody ProfileRequest request
                                                        , @RequestHeader("Authorization") String token) {
        profileService.updateProfile(id, request, token);
        return ResponseEntity.ok(ApiResponse.success(id));
    }



    @PostMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필썸네일 파일 업로드",
        description = """
                        ★프로필 아바타 이미지 업로드</br>
                        {host}/api/profile/{filename}/avatar
                        """)
    public ResponseEntity<ApiResponse>  avatarUpload( @PathVariable Long id
                                                    , @ModelAttribute @Valid MultipartFile avatar
                                                    , @RequestHeader("Authorization") String token) {
        String avatarUrl = profileService.uploadAvatarFile(id, avatar, token);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    @Operation(summary = "썸네일 조회",
        description = """
                        ★프로필 아바타 이미지 조회</br>
                        {host}/api/profile/file/{filename}
                        """)
    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return profileService.serveFile(filename);
    }

}
