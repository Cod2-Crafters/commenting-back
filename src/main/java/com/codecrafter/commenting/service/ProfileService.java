package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.jwt.TokenProvider;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.request.ProfileRequest;
import com.codecrafter.commenting.domain.response.ProfileResponse;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.repository.ProfileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberAuthRepository memberAuthRepository;
    private final TokenProvider tokenProvider;


    public ProfileResponse getProfile(Long id) {
        MemberInfo memberInfo = profileRepository.findById(id)
                                                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));

        return retProfileResponse(memberInfo);
    }

    @Transactional(readOnly = false)
    public ProfileResponse updateProfile(Long id, ProfileRequest request, String token) {
        MemberInfo memberInfo = validateMember(id, token);

        memberInfo.setNickname(request.nickname());
        memberInfo.setIntroduce(request.introduce());
        memberInfo.setLink1(request.link1());
        memberInfo.setLink2(request.link2());
        memberInfo.setLink3(request.link3());

        memberInfo.setAllowAnonymous(request.allowAnonymous());
        memberInfo.setEmailNotice(request.emailNotice());

//        memberInfo.update(request.email());
        memberInfo = profileRepository.save(memberInfo);

        try {
            profileRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("프로필을 업데이트할 수 없습니다.");
        }

        return retProfileResponse(memberInfo);
    }

    @Transactional
    public String uploadAvatarFile(Long id, MultipartFile avatarFile, String token) {
        MemberInfo memberInfo = validateMember(id, token);

        String uploadDir = System.getProperty("user.dir") + "/avatar";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = avatarFile.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            Path path = Paths.get(uploadDir, newFilename);
            Files.copy(avatarFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            memberInfo.setAvatarPath(path.toString());
            profileRepository.save(memberInfo);
            return path.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드 실패", e);
        }
    }

    private MemberInfo validateMember(Long id, String token) {
        String cstToken = token.substring(7);
        Long authenticatedUserId = tokenProvider.getUserIdFromToken(cstToken);

        if (!id.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("자신의 프로필만 수정할 수 있습니다.");
        }

        MemberAuth memberAuth = memberAuthRepository.findById(id)
                                                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

        MemberInfo memberInfo = memberAuth.getMemberInfo();

        if (memberInfo == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다");
        }

        return memberInfo;
    }

    private ProfileResponse retProfileResponse(MemberInfo memberInfo) {
        return new ProfileResponse(
            memberInfo.getEmail(),
            memberInfo.getNickname(),
            memberInfo.getIntroduce(),
            memberInfo.getLink1(),
            memberInfo.getLink2(),
            memberInfo.getLink3(),
            memberInfo.getAvatarPath(),
            memberInfo.getAllowAnonymous(),
            memberInfo.getEmailNotice(),
            1L,
            2L  // 개발 전 좋와요수,답변수 하드코딩
        );
    }
}
