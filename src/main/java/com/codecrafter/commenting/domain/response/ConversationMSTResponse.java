package com.codecrafter.commenting.domain.response;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import com.codecrafter.commenting.domain.entity.ConversationMST;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class ConversationMSTResponse {

	private Long id;

	private Long ownerId;

	private Long memberId;

	private List<ConversationResponse> conversations = new ArrayList<>();

	public static ConversationMSTResponse fromEntity(ConversationMST conversationMST) {
		ConversationMSTResponse conversationMSTResponse = new ConversationMSTResponse();
		conversationMSTResponse.id = conversationMST.getId();
		conversationMSTResponse.ownerId = conversationMST.getOwner().getId();
		conversationMSTResponse.conversations = conversationMST.getConversations().stream()
			.map(ConversationResponse::fromEntity)
			.toList();
		return conversationMSTResponse;

	}
}
