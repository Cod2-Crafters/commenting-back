package com.codecrafter.commenting.domain.response.conversation;

public interface ConversationDetailsResponse {
    Long getGuestId();
    Long getOwnerId();
    String getContent();
    Boolean getIsGood();
    Boolean getIsPrivate();
    Boolean getIsQuestion();
}