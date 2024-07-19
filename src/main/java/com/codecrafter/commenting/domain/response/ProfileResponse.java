package com.codecrafter.commenting.domain.response;


import com.codecrafter.commenting.domain.entity.MemberInfo;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileResponse(
//    @Schema(description = "이메일", example = "jayce@crafter.com")
//    String email,
//    @Schema(description = "닉네임", example = "제이스")
//    String nickname,
//    @Schema(description = "소개", example = "난 제이스야")
//    String introduce,
//    @Schema(description = "링크1", example = "/인스타그램주소/")
//    String link1,
//    @Schema(description = "링크2", example = "/블로그1주소/")
//    String link2,
//    @Schema(description = "링크3", example = "/블로그2주소/")
//    String link3,
//    @Schema(description = "썸네일", example = "/프로필이미지경로/")
//    String avatarPath,
//    @Schema(description = "익명댓글허용여부", example = "true")
//    Boolean allowAnonymous,
//    @Schema(description = "메일수신여부", example = "true")
//    Boolean emailNotice,
//    @Schema(description = "좋와요수", example = "1")
//    Long isGoodCnt,
//    @Schema(description = "답변수", example = "2")
//    Long isAnswerCnt
    MemberInfo memberInfo, Long answerCnt, Long likeCnt
) {

}
