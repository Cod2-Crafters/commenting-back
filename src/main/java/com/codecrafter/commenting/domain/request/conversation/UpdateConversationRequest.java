package com.codecrafter.commenting.domain.request.conversation;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateConversationRequest(
    @Schema(description = "대화 아이디", example = "1")
    Long conId,
    @Schema(description = "질문 내용", example = "퇴사하고싶어요")
    String content,
    @Schema(description = "노출여부(보여줄거면 true)", example = "true")
    boolean isPrivate,
    @Schema(description = "질문여부(질문이면 true/주인장의 답변이면 false)", example = "true")
    boolean isQuestion
) {

}