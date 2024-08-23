package com.codecrafter.commenting.domain.request.conversation;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateGlobalQuestionRequest(
    @Schema(description = "질문 내용", example = "광역 질문하는 중이에요")
    String question
) {

}
