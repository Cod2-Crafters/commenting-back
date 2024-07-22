package com.codecrafter.commenting.domain.response;


import com.codecrafter.commenting.domain.entity.MemberInfo;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileResponse(
    MemberInfo memberInfo, Long answerCnt, Long likeCnt
) {

}
