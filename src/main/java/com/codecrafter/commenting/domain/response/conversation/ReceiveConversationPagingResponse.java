package com.codecrafter.commenting.domain.response.conversation;

import java.util.List;

public record ReceiveConversationPagingResponse(
    List<ReceiveConversationResponse> conversations,
    boolean lastPage
) {
    public ReceiveConversationPagingResponse(List<ReceiveConversationResponse> conversations, boolean lastPage) {
        this.conversations = conversations;
        this.lastPage = lastPage;
    }
}
