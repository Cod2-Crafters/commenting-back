package com.codecrafter.commenting.domain.response;

import static lombok.AccessLevel.*;

import com.codecrafter.commenting.domain.entity.Conversation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class ConversationResponse {

	private Long id;

	private String content;

	private boolean isGood;

	private boolean isQuestion;

	public static ConversationResponse fromEntity(Conversation conversation) {
		ConversationResponse conversationResponse = new ConversationResponse();
		conversationResponse.id = conversation.getId();
		conversationResponse.content = conversation.getContent();
		conversationResponse.isGood = conversation.getIsGood();
		conversationResponse.isQuestion = conversation.isQuestion();
		return conversationResponse;
	}

}
