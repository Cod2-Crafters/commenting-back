package com.codecrafter.commenting.domain.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GoodQuestionResponse(
    LocalDateTime createdAt,
    String content,
    String ownerNickName,
    String guestNickname,
    Long mstId,
    Long guestId,
    Long ownerId
) {

}