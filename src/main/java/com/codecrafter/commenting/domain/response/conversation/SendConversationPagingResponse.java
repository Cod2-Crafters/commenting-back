package com.codecrafter.commenting.domain.response.conversation;

import java.util.List;

public record SendConversationPagingResponse(
    List<SendConversationResponse> conversations,
    boolean lastPage
) {
    public SendConversationPagingResponse(List<SendConversationResponse> conversations, boolean lastPage) {
        this.conversations = conversations;
        this.lastPage = lastPage;
    }
}
