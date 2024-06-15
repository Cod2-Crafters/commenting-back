package com.codecrafter.commenting.domain.request;

public record ProfileRequest(
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
