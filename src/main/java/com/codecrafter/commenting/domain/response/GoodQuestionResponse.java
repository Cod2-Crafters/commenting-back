package com.codecrafter.commenting.domain.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GoodQuestionResponse(
    LocalDateTime createAt,
    String content,
    String writerNickName,
    String receiverNickName,
    Long MstId
) {

}