package com.codecrafter.commenting.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecommendRequest (
    @Schema(description = "추천한 대화 ID", example = "1")
    Long conId,
    @Schema(description = "추천을 누른 계정 ID", example = "1")
    Long userId
){

}
