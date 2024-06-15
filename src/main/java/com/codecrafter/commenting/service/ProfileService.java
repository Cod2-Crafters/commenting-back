package com.codecrafter.commenting.service;

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
    private final MemberInfoRepository memberInfoRepository;
    public ProfileResponse getProfile(Long id) {
        log.info("id : ", id);
        MemberInfo memberInfo = profileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));

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
            "token"
        );
    }

    @Transactional(readOnly = false)
    public ProfileResponse updateProfile(Long id, ProfileRequest request) {
        MemberAuth memberAuth = memberAuthRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        MemberInfo memberInfo = memberAuth.getMemberInfo();

        if (memberInfo == null) {
            throw new IllegalArgumentException("MemberInfo not found for the member");
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
        memberInfo = memberInfoRepository.save(memberInfo);

        try {
            memberInfoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error updating profile.");
        }

        return new ProfileResponse(
            memberAuth.getEmail(),
            memberInfo.getNickname(),
            memberInfo.getIntroduce(),
            memberInfo.getLink1(),
            memberInfo.getLink2(),
            memberInfo.getLink3(),
            memberInfo.getAvatarPath(),
            memberInfo.getAllowAnonymous(),
            memberInfo.getEmailNotice(),
            request.token()
        );
    }
}
