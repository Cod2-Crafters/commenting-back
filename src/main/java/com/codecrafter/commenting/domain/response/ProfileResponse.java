package com.codecrafter.commenting.domain.response;


import com.codecrafter.commenting.domain.entity.MemberInfo;

public record ProfileResponse(
    String email,
    String nickname,
    String introduce,
    String link1,
    String link2,
    String link3,
    String avatarPath,
    Boolean allowAnonymous,
    Boolean emailNotice,
    String token
) {



}
