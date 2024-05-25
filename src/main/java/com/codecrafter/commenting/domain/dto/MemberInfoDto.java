package com.codecrafter.commenting.domain.dto;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoDto {
    private String email;
    private String nickname;
    private String introduce;
    private String link1;
    private String link2;
    private String link3;
    private String avatarPath;
    private Boolean allowAnonymous;
    private Boolean emailNotice;

    public MemberInfo toMemberInfo() {
        return MemberInfo.builder()
            .email(email)
            .nickname(nickname)
            .introduce(introduce)
            .link1(link1)
            .link2(link2)
            .link3(link3)
            .avatarPath(avatarPath)
            .allowAnonymous(allowAnonymous)
            .emailNotice(emailNotice)
            .build();
    }


}
