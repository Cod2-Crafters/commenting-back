package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.MemberSetting;
import com.codecrafter.commenting.domain.enumeration.SettingName;
import com.codecrafter.commenting.domain.response.SettingDataResponse;
import com.codecrafter.commenting.domain.response.SettingResponse;
import com.codecrafter.commenting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberSettingService {

    private final MemberSettingRepository memberSettingRepository;

    @Transactional(readOnly = true)
    public SettingDataResponse getSettingData() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();
        MemberSetting memberSetting = memberInfo.getMemberSetting();

        return SettingDataResponse.builder()
                                .allowAnonymous(memberSetting.getAllowAnonymous())
                                .emailNotice(memberSetting.getEmailNotice())
                                .isSpacePaused(memberSetting.getIsSpacePaused())
                                .allowGlobalQuestion(memberSetting.getAllowGlobalQuestion())
                                .build();
    }

    @Transactional
    public SettingResponse setAllowGlobalQuestion() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();
        MemberSetting memberSetting = memberInfo.getMemberSetting();
        if (memberSetting.getAllowGlobalQuestion()) {
            memberSetting.setAllowGlobalQuestion(false);
        } else {
            memberSetting.setAllowGlobalQuestion(true);
        }
        memberSettingRepository.save(memberSetting);
        return new SettingResponse(SettingName.ALLOW_GLOBAL_QUESTION, memberSetting.getAllowGlobalQuestion());
    }

    @Transactional
    public SettingResponse setEmailNotification() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();
        MemberSetting memberSetting = memberInfo.getMemberSetting();
        if (memberSetting.getEmailNotice()) {
            memberSetting.setEmailNotice(false);
        } else {
            memberSetting.setEmailNotice(true);
        }
        memberSettingRepository.save(memberSetting);
        return new SettingResponse(SettingName.ALLOW_EMAIL_NOTIFICATION, memberSetting.getEmailNotice());
    }

    @Transactional
    public SettingResponse setSpacePause() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();
        MemberSetting memberSetting = memberInfo.getMemberSetting();
        if (memberSetting.getIsSpacePaused()) {
            memberSetting.setIsSpacePaused(false);
        } else {
            memberSetting.setIsSpacePaused(true);
        }
        memberSettingRepository.save(memberSetting);
        return new SettingResponse(SettingName.SPACE_PAUSE, memberSetting.getIsSpacePaused());
    }

    @Transactional
    public SettingResponse setAllowAnonymous() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();
        MemberSetting memberSetting = memberInfo.getMemberSetting();
        if (memberSetting.getAllowAnonymous()) {
            memberSetting.setAllowAnonymous(false);
        } else {
            memberSetting.setAllowAnonymous(true);
        }
        memberSettingRepository.save(memberSetting);
        return new SettingResponse(SettingName.ALLOW_ANONYMOUS_QUESTION, memberSetting.getAllowAnonymous());
    }
}
