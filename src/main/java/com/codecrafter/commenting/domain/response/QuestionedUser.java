package com.codecrafter.commenting.domain.response;

import lombok.Getter;

@Getter
public class QuestionedUser {
    private String avatarPath;
    private Long memberId;

    public QuestionedUser(QuestionedUserInterface questionedUserInterface) {
        this.avatarPath = questionedUserInterface.getAvatarPath();
        this.memberId = questionedUserInterface.getMemberId();
    }
}
