package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.jwt.TokenProvider;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.request.ProfileRequest;
import com.codecrafter.commenting.domain.response.ProfileResponse;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String cstToken = token.substring(7);
        Long authenticatedUserId = tokenProvider.getUserIdFromToken(cstToken);

        log.info("updateProfile1 : {}", token);
        log.info("updateProfile2 : {}", authenticatedUserId);

        if (!id.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("자신의 프로필만 수정할 수 있습니다.");
        }
        MemberAuth memberAuth = memberAuthRepository.findById(id)
                                                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

        MemberInfo memberInfo = memberAuth.getMemberInfo();

        if (memberInfo == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다");
        }

        memberInfo.setNickname(request.nickname());
        memberInfo.setIntroduce(request.introduce());
        memberInfo.setLink1(request.link1());
        memberInfo.setLink2(request.link2());
        memberInfo.setLink3(request.link3());
        memberInfo.setAvatarPath(request.avatarPath());
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
            memberInfo.getEmailNotice()
        );
    }
}
