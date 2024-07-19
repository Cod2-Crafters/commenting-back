package com.codecrafter.commenting.domain.dto;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {
    private Long id;
    private String email;
    private String nickname;
    private String introduce;
    private String link1;
    private String link2;
    private String link3;
    private String avatarPath;
    private Boolean allowAnonymous;
    private Boolean emailNotice;
    private Long answerCnt;
    private Long likesCnt;

    public MemberInfoDto(MemberInfo memberInfo, Long answerCnt, Long likesCnt) {
        this.id = memberInfo.getId();
        this.email = memberInfo.getEmail();
        this.nickname = memberInfo.getNickname();
        this.introduce = memberInfo.getIntroduce();
        this.link1 = memberInfo.getLink1();
        this.link2 = memberInfo.getLink2();
        this.link3 = memberInfo.getLink3();
        this.avatarPath = memberInfo.getAvatarPath();
        this.allowAnonymous = memberInfo.getAllowAnonymous();
        this.emailNotice = memberInfo.getEmailNotice();
        this.answerCnt = answerCnt;
        this.likesCnt = likesCnt;
    }
}
