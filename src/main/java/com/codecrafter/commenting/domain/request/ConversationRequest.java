package com.codecrafter.commenting.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ConversationRequest(

	@NotBlank
	Long conversationMSTID,

	@NotEmpty(message = "빈 값을 전송할 수 없음")
	String content,

	boolean isPrivate

) {
}
