package com.codecrafter.commenting.domain.response;

import com.codecrafter.commenting.domain.entity.MemberInfo;

public record MemberInfoResponse(
    Long id,
    String avatarPath,
    String nickName
) {
    public static MemberInfoResponse from(MemberInfo memberInfo) {
        return new MemberInfoResponse(memberInfo.getId(), memberInfo.getAvatarPath(), memberInfo.getNickname());
    }
}
